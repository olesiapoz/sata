# sata

The SLA-adaptive threshold adjustment algorithm for Kubernetes Horizontal Autoscaler

The repo contains the solution prototype files used for experimental evalution. To reproduce the setup deploy the solution on Azue Kubernetes service. AKS version 1.29 is the latest version on which the setup was tested.

To test the setup you need:
* helm
* maven
* gatling v3.9.0
* custom pod autoscaler version 1.2.0 (see /Users/opo/codes/sata/custom-pod-autoscaler/readme.txt)
* docker

You need helm to intall infrastructure using scripts proided in Infra scrips. To work on different AKS cluster the following needs to be updated:

* values.yaml in Prometheus folder:

```yaml
scrape_configs:
      - job_name: prometheus
        static_configs:
          - targets:
            <enter your azure load balancer hostname here>
            #e.g. - phd-demo1.swedencentral.cloudapp.azure.com

...

## External URL which can access alertmanager
  baseURL: <your url here>:9093
  #e.g. "http://phd-demo1.swedencentral.cloudapp.azure.com:9093"
```
* update loadbalncer URL in gatling simulation java files located in gatling-charts-highcharts-bundle-3.9.0/user-files/simulations/computerdatabase :

```java
HttpProtocolBuilder httpProtocol = http // 4
      .baseUrl(<your URL here>)
```

* Maven is required to rebuild autoscaler or use existing images from dockerhub (image names used in experiments for solution can be found in deployment files).

Now you can try to rebuild the environment  by running Infra scripts/install-thresholds.sh  script in sudo mode.
