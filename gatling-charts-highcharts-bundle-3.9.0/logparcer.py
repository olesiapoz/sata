import csv
import math, time, datetime
import  digitalocean
from digitalocean_env import *
from pathlib import Path

t_scrape = int(6000)
t_req_timeout = int(3000)
t_rps_evaluation = int(30000)
t_resolution = int(10000)
t_slo = int(1000)
bt_max = 19
rps_peak = 400
SLO_tgt = 95
SLO_BUDGET_RPS = 0

print("starting")

def update_SLO_budget_rps(rps_per_period,rps_per_period_ko, SLO_BUDGET_RPS):
    if rps_per_period > 0:
        slo_n = ((rps_per_period-rps_per_period_ko)/rps_per_period)*100
        SLO_BUDGET_RPS += rps_per_period*(slo_n - SLO_tgt)
    return SLO_BUDGET_RPS
    #print("Current SLO budget is: {0}".format(SLO_BUDGET_RPS))

def calculate_positive_pods(rps_per_period,rps_per_period_ko, slo_last_eval_period):
    slo_n = 0
    positive_pods = 0
    rps = 0
    if rps_per_period > 0:
        slo_n = (rps_per_period-rps_per_period_ko)/rps_per_period*100
        rps = rps_per_period*(slo_n-SLO_tgt)/100
        #print("SLO_RPS {0}".format(slo_last_eval_period ))
        if slo_last_eval_period< SLO_tgt :
            positive_pods = math.ceil(rps/bt_max) if rps > 0 else 0
        else:
            positive_pods = math.ceil(rps/bt_max) if (rps < 0 and (SLO_BUDGET_RPS + rps)) > 0 else 0
    return positive_pods
    


# open .tsv files
with open("simulation.log") as file:
       
    # Passing the TSV file to 
    # reader() function
    # with tab delimiter
    # This function will
    # read data from file
    tsv_file = csv.reader(file, delimiter="\t")

    requests = filter(lambda r: r[0] == "REQUEST", tsv_file)

    statistics = []
    line_nr = 0
    ok = 0
    
    for r in requests:
        rt = int(r[4])-int(r[3])
        line_nr = line_nr + 1
        if rt <= t_slo:
            ok = ok + 1
        
        r.append(rt)
        slo = ok/line_nr*100
        r.append(slo)
        r.append(ok)
        r.append(line_nr)
        statistics.append(r)
    

    #REQUEST,,reque,1677607057460,1677607058288,OK, ,828,100.0,1,1
    request_list = list(statistics)

    t_start = int(request_list[0][4])-t_rps_evaluation
    t_end = int(request_list[-1][4])
    t_period_end = int(request_list[0][4])
    rps_iterations = math.ceil((t_end-t_start)/t_scrape)

    rps_per_period_list = []
    #print(len(request_list))
    periodic_list = []

    rps_batch_size = math.ceil(rps_peak*(t_rps_evaluation + t_scrape)*2/1000)
    end_index = rps_batch_size 
    #print(end_index)
    start_index = 0
    future_start_index  = 0

    i = 0
    

    while i < rps_iterations: 
        
        time_0 = time.time()
        #req_last_eval_period_total = list(filter(lambda rps: (t_start ) <= int(rps[4]) < t_period_end, request_list))
        req_last_eval_period_total = [ rps for rps in request_list[start_index:end_index] if (t_start ) <= int(rps[4]) < t_period_end ]
        time_1 = time.time()
        #print("Start: {0}, End: {1}, diff :{2}".format(t_start, t_period_end, t_start-t_period_end))
        
        if len(req_last_eval_period_total) > 0:
            slo_last_eval_period = req_last_eval_period_total[0][8]
            ok_last_eval_period = req_last_eval_period_total[0][9]
            line_nr_last_eval_period = req_last_eval_period_total[0][10]
            unixToDatetime = datetime.datetime.fromtimestamp(math.floor(int(t_period_end))/1000)

            previuos_start_index = future_start_index
            future_start_index = line_nr_last_eval_period
            end_index = end_index + future_start_index-previuos_start_index
            start_index = previuos_start_index - rps_batch_size

            if start_index < 0:
                start_index = 0
        

            if end_index > len(request_list):
                end_index = len(request_list)   

                
            
            #print("S: {0}, E: {1}".format(start_index, end_index))

            time_0 = time.time()
            req_last_eval_period_ko = [ ko for ko in req_last_eval_period_total if  ko[5] != "OK" ]
            #req_last_eval_period_ko = list(filter(lambda ko:  ko[7] != "OK", req_last_eval_period_total))
            time_1 = time.time()
            # print("KO: {0}".format(time_1-time_0))
            #print("Req in list {0}, Lines processed so far {1} out of {2}".format(len(req_last_eval_period_total), line_nr_last_eval_period, len(request_list)))

            # rps_per_period_ko = math.ceil(len(req_last_eval_period_ko)/t_rps_evaluation*1000)
            #rps_per_period = math.ceil(len(req_last_eval_period_total)/t_rps_evaluation*1000)
            rps_per_period_ko = round(len(req_last_eval_period_ko)/t_rps_evaluation*1000,0)
            rps_per_period = round(len(req_last_eval_period_total)/t_rps_evaluation*1000,0)

            

            #print("Positive pods: {0}".format(calculate_positive_pods(rps_per_period, rps_per_period_ko, slo_last_eval_period)))
            rps_per_period_list.append(unixToDatetime)
            rps_per_period_list.append(rps_per_period)
            rps_per_period_list.append(rps_per_period_ko)
            rps_per_period_list.append(rps_per_period - rps_per_period_ko)
            rps_per_period_list.append(len(req_last_eval_period_total))
            rps_per_period_list.append(len(req_last_eval_period_ko))
            rps_per_period_list.append(slo_last_eval_period)
            rps_per_period_list.append(ok_last_eval_period)
            rps_per_period_list.append(line_nr_last_eval_period)
            rps_per_period_list.append(math.ceil(rps_per_period/bt_max))
            rps_per_period_list.append(update_SLO_budget_rps(rps_per_period, rps_per_period_ko, SLO_BUDGET_RPS))
            rps_per_period_list.append(calculate_positive_pods(rps_per_period, rps_per_period_ko, slo_last_eval_period))
            
            periodic_list.append(rps_per_period_list)

        t_start = t_start + t_scrape
        t_period_end = t_start + t_rps_evaluation 
        #print(rps_per_period_list)

        rps_per_period_list = []
        i = i + 1    
    #print(periodic_list)


with open("results (5).csv") as file:
       
    # Passing the TSV file to 
    # reader() function
    # with tab delimiter
    # This function will
    # read data from file
    autoscaler_logs = csv.reader(file, delimiter=",")
    autoscaler_logs_list = list(autoscaler_logs)
    log_start_index = 0
    for l in autoscaler_logs_list:
        if l[-2] == "NaN":
            log_start_index += 1
        else:
            break


    ready_pods_list = []
    final_list = []
    
    for l in autoscaler_logs_list[log_start_index+1:len(periodic_list)] :
        ready_pods_list.append(l[5])
    
    print(log_start_index)


    i = 0
    while (i < len(periodic_list) and i < len(ready_pods_list)):
        print(i)
        periodic_list[i].append(ready_pods_list[i])
        i += 1
    


with open('statistics.csv', 'w', newline='') as f:
    # create the csv writer
        writer = csv.writer(f)

    # write a row to the csv file
        writer.writerows(periodic_list)

with open('raw_statistics.csv', 'w', newline='') as f:
    # create the csv writer
        writer = csv.writer(f)

    # write a row to the csv file
        writer.writerows(statistics)



client = digitalocean.get_spaces_client(
    region_name= region,
    endpoint_url=url,
    key_id=key,
    secret_access_key=secret,
    is_public=True,
    content_type="text/plain"
)

print("uploading")

p = Path('simulation.log')
p.rename(p.with_suffix('.tsv'))

#digitalocean.upload_file_to_space(client, "phd", "raw_statistics.csv", str(datetime.date.today()) + "raw_statistics.csv")
digitalocean.upload_file_to_space(client, "phd", "raw_statistics.csv",  datetime.datetime.today().strftime("%a_%d_%H_%M") +"/"+ "raw_statistics.csv")
digitalocean.upload_file_to_space(client, "phd", "statistics.csv",  datetime.datetime.today().strftime("%a_%d_%H_%M") +"/"+ "statistics.csv")
digitalocean.upload_file_to_space(client, "phd", "simulation.tsv",  datetime.datetime.today().strftime("%a_%d_%H_%M") +"/"+ "simulation.tsv")

p = Path('simulation.tsv')
p.rename(p.with_suffix('.log'))

print("done")