/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Mnesymne
 */
public interface IServerSecurity extends Remote {
    
    /**
     * Pre-condition: User must be in a lobby.
     * Description: Returns user from the lobby to the Lobby Select screen if the user has been AFK for too long.
     * @throws RemoteException 
     */
    public void returnToLobbySelect()throws RemoteException;
}
