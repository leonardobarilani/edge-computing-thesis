package cli.infrastucture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OpenFaaSRedisConfiguration {

    public String openfaas_gateway;
    public String openfaas_password;
    public String redis_host;
    public int redis_port;
    public String redis_password;

    /* probably will be removed */
    public String location_id;


    //todo autofill as a long list of nested locations, as many as the nodes deployed
    public void autoFillMissing(String areaName) throws IOException {
        String scriptGateway =
                "kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}' --context=" + areaName;

        if (openfaas_gateway == null) {
            String gateway_ip = printOutput(Runtime.getRuntime().exec(scriptGateway));
            if (gateway_ip == null) {
                System.out.println("(OpenFaaSRedisConfiguration.autoFillMissing) Cannot autofill openfaas_gateway for area <" + areaName + ">: failed execution of: " + scriptGateway);
                System.out.println("(OpenFaaSRedisConfiguration.autoFillMissing) Auto-filling field openfaas_gateway with: autofilled-gateway");
                openfaas_gateway = "autofilled-gateway";
            } else {
                openfaas_gateway = "http://" + gateway_ip.substring(1, gateway_ip.length() - 1) + ":31112";
            }
        }
        if (openfaas_password == null)
            openfaas_password = "autofilled-password";
        if (redis_host == null)
            redis_host = "my-openfaas-redis-master.openfaas-fn.svc.cluster.local";
        if (redis_port == 0)
            redis_port = 6379;
        if (redis_password == null)
            redis_password = "autofilled-password";
    }

    private String printOutput(Process proc) {
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));
        String s = null;
        try {
            if ((s = stdInput.readLine()) != null) {
                return s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
