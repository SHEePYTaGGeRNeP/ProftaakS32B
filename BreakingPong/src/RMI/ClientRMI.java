/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import Interfaces.IGame;
import RMIPaddleMoveTest.Stub;
import Shared.Ball;
import Shared.Block;
import Shared.Paddle;
import fontys.observer.RemotePropertyListener;
import fontys.observer.RemotePublisher;
import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Jordi
 */
public class ClientRMI extends UnicastRemoteObject implements RemotePropertyListener
{

    private IGame game;
    private RemotePublisher publisher;
    private Client client;
    private long timeOut;
    private Registry reg;
    private String newGameTime;
    private Text gameTimeLabel;
    private Text fpsLabel;
    private Text scoreLabel1;
    private Text scoreLabel2;
    private Text scoreLabel3;
    private Text scoreLabel4;
    private Text username1;
    private Text username2;
    private Text username3;
    private Text username4;
    long nextSecond = System.currentTimeMillis() + 1000;
    int frameInLastSecond = 0;
    int framesInCurrentSecond = 0;

    public ClientRMI(Client client) throws RemoteException
    {
        this.client = client;
        this.start();
    }

    public void start()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (Platform.isImplicitExit())
                {
                    super.cancel();
                }

                if (System.currentTimeMillis() - timeOut > 10 * 1000 || publisher == null)
                {
                    System.out.println("Attempting to setup a connection");
                    try
                    {
                        connect();
                        System.out.println("Connected!");
                    } catch (Exception ex)
                    {
                        Logger.getLogger(Stub.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }, 0, 2500);
    }

    public void stop()
    {
        try
        {
            if (this.publisher != null)
            {
                this.publisher.removeListener(this, "getBlocks");
                this.publisher.removeListener(this, "getTime");
                this.publisher.removeListener(this, "getBalls");
                this.publisher.removeListener(this, "getPaddles");
                this.publisher.removeListener(this, "getGameOver");
            }
            UnicastRemoteObject.unexportObject(this, true);
        } catch (RemoteException ex)
        {
            Logger.getLogger(Stub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect()
    {
        try
        {
            this.reg = LocateRegistry.getRegistry("127.0.0.1", 1098);
            this.publisher = (RemotePublisher) this.reg.lookup("gameServer");
            this.publisher.addListener(this, "getBlocks");
            this.publisher.addListener(this, "getTime");
            this.publisher.addListener(this, "getBalls");
            this.publisher.addListener(this, "getPaddles");
            this.publisher.addListener(this, "getGameOver");
            this.client.connection.joinGame(1, client.Name);
            System.out.println("Game joined");
        } catch (RemoteException | NotBoundException ex)
        {
            Logger.getLogger(Stub.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            this.timeOut = System.currentTimeMillis();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        //Draw all blocks from server
        if (evt.getPropertyName().equals("getBlocks"))
        {
            client.blockList = new ArrayList<>();
            Block[] blocks = (Block[]) evt.getNewValue();
            for (int i = 0; i < blocks.length; i++)
            {
                client.blockList.add(blocks[i]);
            }
            //System.out.println(client.blockList.size() + new Date().toString());
        }
        //Draw all balls from server
        if (evt.getPropertyName().equals("getBalls"))
        {
            client.ballList = new ArrayList<>();
            client.ballList = (ArrayList<Ball>) evt.getNewValue();
        }
        //Draw all paddles from server
        if (evt.getPropertyName().equals("getPaddles"))
        {
            client.paddleList = new ArrayList<>();
            client.paddleList = (ArrayList<Paddle>) evt.getNewValue();
        }
        // Get the gametime from server
        if (evt.getPropertyName().equals("getTime"))
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    newGameTime = evt.getNewValue().toString();
                }
            });
        }

        if (evt.getPropertyName().equals("getGameOver"))
        {
            try
            {
                System.out.println("GameOver!");
                stop();
                client.shutDown();
                return;
            } catch (Exception ex)
            {
                Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        drawGame();
    }

    public void drawGame()
    {

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                client.root.getChildren().clear();
            }
        });

        if (client.paddleList != null)
        {
            for (Paddle paddle : client.paddleList)
            {
                Rectangle r = new Rectangle(paddle.getPosition().getX(), paddle.getPosition().getY(), paddle.getSize().getX(), paddle.getSize().getY());
                r.setFill(Color.GREEN);
                r.setStroke(Color.BLACK);
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        client.root.getChildren().add(r);
                    }
                });
            }
        }

        if (client.ballList != null)
        {
            for (Ball ball : client.ballList)
            {
                Circle c = new Circle(ball.getPosition().getX(), ball.getPosition().getY(), 5);
                c.setStroke(Color.BLACK);
                c.setFill(Color.LIGHTBLUE);
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        client.root.getChildren().add(c);
                    }
                });
            }
        }

        if (client.blockList != null)
        {
            for (Block block : client.blockList)
            {
                Rectangle r = new Rectangle(block.getPosition().getX(), block.getPosition().getY(), block.getSize().getX(), block.getSize().getY());
                r.setStroke(Color.BLACK);
                if (block.isDestructable() == false)
                {
                    r.setFill(Color.DARKGRAY);
                } else
                {
                    if (block.getPowerUp() == null)
                    {
                        r.setFill(Color.YELLOW);
                    } else
                    {
                        r.setFill(Color.RED);
                    }
                }
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        client.root.getChildren().add(r);
                    }
                });
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime > nextSecond)
        {
            nextSecond += 1000;
            frameInLastSecond = framesInCurrentSecond;
            framesInCurrentSecond = 0;
        }
        framesInCurrentSecond++;

        Platform.runLater(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    gameTimeLabel = new Text(25, 25, "Time Left: " + newGameTime);
                    gameTimeLabel.setFont(Font.font("Verdana", 20));
                    gameTimeLabel.setFill(Color.WHITE);
                    client.root.getChildren().add(gameTimeLabel);
                    
                    fpsLabel = new Text(25, 130, "FPS " + frameInLastSecond);
                    fpsLabel.setFont(Font.font("Verdana", 20));
                    fpsLabel.setFill(Color.WHITE);
                    client.root.getChildren().add(fpsLabel);
                    
//                    scoreLabel1 = new Text(25, 50, "Score1: " + client.paddleList.get(0).getScore());
//                    scoreLabel1.setFont(Font.font("Verdana", 20));
//                    scoreLabel1.setFill(Color.WHITE);
//                    client.root.getChildren().add(scoreLabel1);
//                    
//                    scoreLabel2 = new Text(25, 70, "Score2: " + client.paddleList.get(1).getScore());
//                    scoreLabel2.setFont(Font.font("Verdana", 20));
//                    scoreLabel2.setFill(Color.WHITE);
//                    client.root.getChildren().add(scoreLabel2);
//                    
//                    scoreLabel3 = new Text(25, 90, "Score3: " + client.paddleList.get(2).getScore());
//                    scoreLabel3.setFont(Font.font("Verdana", 20));
//                    scoreLabel3.setFill(Color.WHITE);
//                    client.root.getChildren().add(scoreLabel3);
//                    
//                    scoreLabel4 = new Text(25, 110, "Score4: " + client.paddleList.get(3).getScore());
//                    scoreLabel4.setFont(Font.font("Verdana", 20));
//                    scoreLabel4.setFill(Color.WHITE);
//                    client.root.getChildren().add(scoreLabel4);
//                    
//                    username1 = new Text(25, 120, "Username1: " + client.paddleList.get(0).getPlayer().getUsername(null));
//                    username1.setFont(Font.font("Verdana", 20));
//                    username1.setFill(Color.WHITE);
//                    client.root.getChildren().add(username1);
                } catch (RemoteException ex)
                {
                    Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
