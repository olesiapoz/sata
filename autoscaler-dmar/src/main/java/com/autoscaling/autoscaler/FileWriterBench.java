package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Service
public class FileWriterBench {

    File avgCPUFILE = new File("/root/BenchResults"+ DateUtils.now() +".csv");
    PrintWriter avgCPUWriter;

    File btCount = new File("bt_count.csv");
    PrintWriter btCountWriter;

    File slaReader = new File("sla.csv");
    PrintWriter slaWriter;

    File podCount = new File("pod_count.csv");
    PrintWriter podContWriter;

    File podUpTime = new File("pod_upTime.csv");
    PrintWriter podUpTimeWriter;



    @PostConstruct
    public void init(){
        try {
            avgCPUWriter = new PrintWriter(avgCPUFILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            btCountWriter = new PrintWriter(btCount);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            slaWriter = new PrintWriter(slaReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            podContWriter = new PrintWriter(podCount);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            podUpTimeWriter = new PrintWriter(podUpTime);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeAll(AvgCPU avgCPU, BTCount btCount, PodCount podCount, SLA sla, PodUpTime podUpTime, BTTotal btTotal,  AvgResponse avgResponse, AvgResponse percentileResponse, TouchstonePodCount touchStonePods,Throughput throughput, AvgCPU currentThreshold, SLA slaAfterLearningPeriod ) {
        avgCPUWriter.println(avgCPU.toCSVLine()+","+btCount.toCSVLine()+","+podCount.toCSVLine()+","+sla.toCSVLine()+","+podUpTime.toCSVLine()+","+btTotal.toCSVLine()+","+avgResponse.toCSVLine()+","+percentileResponse.toCSVLine()+","+touchStonePods.toCSVLine() +","+ throughput.toCSVLine() + "," + currentThreshold.toCSVLine() + "," + slaAfterLearningPeriod.toCSVLine() );
        avgCPUWriter.flush();
    }


    public void writeAvgCPU(AvgCPU avgCPU) {
        avgCPUWriter.println(avgCPU.toCSVLine());
        avgCPUWriter.flush();
    }

    public void writeBTCount(BTCount btCount) {
        btCountWriter.println(btCount.toCSVLine());
        btCountWriter.flush();
    }

    public void writeSLA(SLA sla) {
        slaWriter.println(sla.toCSVLine());
        slaWriter.flush();
    }

    public void writePodCount(PodCount podCount) {
        podContWriter.println(podCount.toCSVLine());
        podContWriter.flush();
    }

    public void writePodUpTime(PodUpTime podUpTime) {
        podUpTimeWriter.println(podUpTime.toCSVLine());
        podUpTimeWriter.flush();
    }
}
