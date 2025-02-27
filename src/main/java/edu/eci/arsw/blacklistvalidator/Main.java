/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]) throws InterruptedException {
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        List<Integer> blackListOcurrences1=hblv.checkHost("202.24.34.55", 200); // Para verificar la eficiencia de los hilos ocn el host mas disperso
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences1);
        List<Integer> blackListOcurrences2=hblv.checkHost("200.24.34.55", 1); // Para verificar la cantidad de listas negras revisadas
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences2);
    }
    
}
