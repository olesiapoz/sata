
use warp::{Filter, Reply, Rejection};

use serde::Deserialize;
use tokio::time::Instant;

use lazy_static::lazy_static;
use prometheus::{HistogramOpts, HistogramVec, IntCounter, Registry};

use rand::Rng;
use rand::rngs::ThreadRng;

use sha2::{Sha256, Digest};
use tokio::time::{sleep, Duration};

lazy_static! {
    pub static ref REGISTRY: Registry = Registry::new();

    pub static ref RESPONSE_TIME_COLLECTOR: HistogramVec = HistogramVec::new(
        HistogramOpts::new("request_duration", "Request Duration")
        .buckets(vec![0.100, 0.125, 0.150, 0.175, 0.200, 0.225, 0.250, 
            0.300, 0.400,
            0.500, 0.750, 1.000, 1.500,  
            2.000, 3.000, 4.000]),
        &[]
    ).expect("metric can't be created");

    pub static ref REQUEST_COUNTER: IntCounter = IntCounter::new(
        "bt_count_total", "BT counter"
    ).expect("metric can't be created");
}

#[tokio::main]
async fn main() {
    REGISTRY
        .register(Box::new(RESPONSE_TIME_COLLECTOR.clone()))
        .expect("histogram can not be registered");
    REGISTRY
        .register(Box::new(REQUEST_COUNTER.clone()))
        .expect("counter can not be registered");

    let metrics_route = warp::path!("metrics").and_then(metrics_handler);
    let work_route = warp::path!("api" / "execute")
        .and(warp::query::<Param>())
        .and_then(work_handler);

    println!("Starting a server");

    warp::serve(metrics_route.or(work_route))
        .run(([0, 0, 0, 0], 30036))
        .await;
}

#[derive(Deserialize, Debug)]
struct Param {
    size_from: usize,
    size_to: usize,
    sleep_from: u64,
    sleep_to: u64,
}

fn gen_numbers(param: &Param) -> (usize, u64) {
    let mut rng = ThreadRng::default();
    let size: usize = if param.size_from <= param.size_to {
        rng.gen_range(param.size_from..param.size_to+1)
    } else {
        0
    };
    let sleep = if param.sleep_from <= param.sleep_to {
        rng.gen_range(param.sleep_from..param.sleep_to+1)
    } else {
        0
    };
    (size, sleep)
}

fn gen_hash(size: usize) -> String {
    let mut rng = ThreadRng::default();
    let mut hasher = Sha256::new();
    (0..size).for_each(|_| {
        let d: u8 = rng.gen();
        hasher.update([d;1]);
    });
    let hash = hasher.finalize();
    format!("{:x}", &hash)
}
async fn work_handler(param: Param) -> Result<impl Reply, Rejection> {
    let instant = Instant::now();
    let (size, sleep_ms) = gen_numbers(&param);
    let sha = gen_hash(size);

    if sleep_ms > 0 {
        sleep(Duration::from_millis(sleep_ms)).await;
    };

    RESPONSE_TIME_COLLECTOR
        .with_label_values(&[])
        .observe(instant.elapsed().as_secs_f64());

    REQUEST_COUNTER.inc();

    Ok(format!("Hash: {}\nSize: {}\nSleep: {}ms\nQuery params: {:?}", sha, size, sleep_ms, param))
}

async fn metrics_handler() -> Result<impl Reply, Rejection> {
    use prometheus::Encoder;
    let encoder = prometheus::TextEncoder::new();

    let mut buffer = Vec::new();
    if let Err(e) = encoder.encode(&REGISTRY.gather(), &mut buffer) {
        eprintln!("could not encode custom metrics: {}", e);
    };
    let res = match String::from_utf8(buffer.clone()) {
        Ok(v) => v,
        Err(e) => {
            eprintln!("custom metrics could not be from_utf8'd: {}", e);
            String::default()
        }
    };
    buffer.clear();

    Ok(res)
}
