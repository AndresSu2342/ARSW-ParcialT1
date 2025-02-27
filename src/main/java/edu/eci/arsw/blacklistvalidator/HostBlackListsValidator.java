/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {
    private int NListasRevisadas = 0;

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        
        int ocurrencesCount=0;
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
        int checkedListsCount=0;
        
        for (int i=0;i<skds.getRegisteredServersCount() && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount++;
            
            if (skds.isInBlackListServer(i, ipaddress)){
                
                blackListOcurrences.add(i);
                
                ocurrencesCount++;
            }
        }
        
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }

    public List<Integer> checkHost(String ipaddress, int NThreads) throws InterruptedException {
        List<SearchHostThread> Threads = new LinkedList<>();
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        int startNHost = 0;
        int intervalNHost = skds.getRegisteredServersCount() / NThreads;
        int finalNHost = intervalNHost;
        int listasRevisadas = 0;

        for (int i=0;i<NThreads;i++){
            SearchHostThread thread = new SearchHostThread(ipaddress, startNHost, finalNHost);
            Threads.add(thread);
            startNHost += intervalNHost;
            if (i==NThreads-2) {
                finalNHost = skds.getRegisteredServersCount();
            }
            else {
                finalNHost += intervalNHost;
            }
        }

        for (SearchHostThread thread: Threads) {
            thread.start();
        }

        for (SearchHostThread thread: Threads) {
            thread.join();
            blackListOcurrences.addAll(thread.getBlackListOcurrences());
            if (blackListOcurrences.size()>=BLACK_LIST_ALARM_COUNT){
                for (SearchHostThread threadAll: Threads) {
                    threadAll.stop();
                    listasRevisadas += threadAll.getNListas();
                }
                skds.reportAsNotTrustworthy(ipaddress);
                break;
            }
        }
        if (blackListOcurrences.size()<BLACK_LIST_ALARM_COUNT) {
            skds.reportAsTrustworthy(ipaddress);
        }
        System.out.println("Número de listas negras revisadas: " + listasRevisadas);
        return blackListOcurrences;
    }

    public List<Integer> checkHost(String ipaddress, int starthost, int finahost){

        LinkedList<Integer> blackListOcurrences=new LinkedList<>();

        int ocurrencesCount=0;

        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();

        for (int i=starthost;i<=finahost && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            NListasRevisadas++;
            if (skds.isInBlackListServer(i, ipaddress)){

                blackListOcurrences.add(i);

                ocurrencesCount++;
            }

        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{starthost, finahost});

        return blackListOcurrences;
    }

    public int getNListasRevisadas() { return NListasRevisadas; }


    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
