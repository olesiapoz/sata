FROM rust:1.78 AS build

WORKDIR /root/

COPY src /root/src/

COPY Cargo.lock /root/

COPY Cargo.toml /root/

RUN rustup target add x86_64-unknown-linux-musl

RUN cargo build --release --target=x86_64-unknown-linux-musl

FROM scratch

COPY --from=build /root/target/x86_64-unknown-linux-musl/release/cat_noir /usr/local/bin/

EXPOSE 30036

CMD ["/usr/local/bin/cat_noir"]