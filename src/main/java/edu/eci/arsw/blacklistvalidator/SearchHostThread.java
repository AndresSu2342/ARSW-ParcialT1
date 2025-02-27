package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.blacklistvalidator.HostBlackListsValidator;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class SearchHostThread extends Thread{
    private int startNHost;
    private int finalNHost;
    private String ipAddress;

    private List<Integer> blackListOcurrences = new LinkedList<>();

    public SearchHostThread(String ipaddress, int starthost, int finahost){
        this.startNHost = starthost;
        this.finalNHost = finahost;
        this.ipAddress = ipaddress;
    }

    public void run() {
        HostBlackListsValidator validator = new HostBlackListsValidator();
        blackListOcurrences = validator.checkHost(ipAddress, startNHost, finalNHost);
    }

    public List<Integer> getBlackListOcurrences() { return blackListOcurrences; }
}
