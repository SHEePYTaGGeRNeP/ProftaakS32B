/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shared;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 *
 * @author Mnesymne
 */
public class CPU
{

    private final String name;
    private final Byte difficulty; // Currently not used
    private Paddle myPaddle; // CPU PADDLE KOPPLING VIA PADDLE KLASSE

    private ArrayList<Ball> allBalls;
    private Game currentGame;
    private Ball closestBall;
    private double[] points;
    private ArrayList<Ball> currentPosBall;

    public CPU(String name, Byte difficulty, Game myGame)
    {
        this.name = name;
        this.difficulty = difficulty;
        this.currentGame = myGame;
    }
    public void setMyPaddle(Paddle myPaddle)
    {
        this.myPaddle = myPaddle;
    }

    public String getName()
    {
        return name;
    }

    public byte getDifficuly()
    {
        return this.difficulty;
    }

    public void startBot()
    {
        Timer timer = new Timer(10, (ActionEvent e) ->
        {
            runCPU();
        });
        timer.start();
    }

    /**
     * Makes the CPU start his AI
     */
    private void runCPU()
    {
        currentPosBall = currentGame.getBallList();
        TVector2 currentPosPaddle = myPaddle.getPosition();
        float[] ClosestTo = new float[8];
        int i = 0;
        for (Ball b : currentPosBall)
        {
            ClosestTo[i] = (b.getPosition().getX() - currentPosPaddle.getX());
            i++;
            ClosestTo[i] = b.getPosition().getY() - currentPosPaddle.getY();
            i++;
        }
        points = new double[4];
        points[0] = Math.sqrt(((ClosestTo[1] * 2) - currentPosPaddle.getY() * 2) + (ClosestTo[0] * 2 - currentPosPaddle.getX() * 2));
        points[1] = Math.sqrt(((ClosestTo[3] * 2) - currentPosPaddle.getY() * 2) + (ClosestTo[2] * 2 - currentPosPaddle.getX() * 2));
        points[2] = Math.sqrt(((ClosestTo[5] * 2) - currentPosPaddle.getY() * 2) + (ClosestTo[4] * 2 - currentPosPaddle.getX() * 2));
        points[3] = Math.sqrt(((ClosestTo[7] * 2) - currentPosPaddle.getY() * 2) + (ClosestTo[6] * 2 - currentPosPaddle.getX() * 2));

        calcPosition();
        Move();
    }

    /**
     * TODO: VUL DEZE SHIT
     */
    public void calcPosition()
    {
        if (points[0] > points[1])
        {
            if (points[0] > points[2])
            {
                if (points[0] > points[3])
                {
                    closestBall = currentPosBall.get(0);
                } else
                {
                    closestBall = currentPosBall.get(3);
                }
            } else if (points[2] > points[3])
            {
                closestBall = currentPosBall.get(2);
            } else
            {
                closestBall = currentPosBall.get(3);
            }
        } else if (points[1] > points[2])
        {
            if (points[1] > points[3])
            {
                closestBall = currentPosBall.get(1);
            } else
            {
                closestBall = currentPosBall.get(3);
            }
        } else if (points[2] > points[3])
        {
            closestBall = currentPosBall.get(2);
        } else
        {
            closestBall = currentPosBall.get(3);
        }
    }

    /**
     * TODO: VUL DEZE SHIT
     */
    public void Move()
    {
        if (this.myPaddle.getWindowLocation() == Paddle.windowLocation.SOUTH)
        {
            if (closestBall.getPosition().getX() > this.myPaddle.getPosition().getX())
            {
                myPaddle.Move(Paddle.direction.RIGHT);
            } else
            {
                myPaddle.Move(Paddle.direction.LEFT);
            }
        } else if (this.myPaddle.getWindowLocation() == Paddle.windowLocation.NORTH)
        {
            if (closestBall.getPosition().getX() < this.myPaddle.getPosition().getX())
            {
                myPaddle.Move(Paddle.direction.RIGHT);
            } else
            {
                myPaddle.Move(Paddle.direction.LEFT);
            }
        } else if (this.myPaddle.getWindowLocation() == Paddle.windowLocation.WEST)
        {
            if (closestBall.getPosition().getY() > this.myPaddle.getPosition().getY())
            {
                myPaddle.Move(Paddle.direction.UP);
            } else
            {
                myPaddle.Move(Paddle.direction.DOWN);
            }
        } else if (this.myPaddle.getWindowLocation() == Paddle.windowLocation.EAST)
        {
            if (closestBall.getPosition().getY() < this.myPaddle.getPosition().getY())
            {
                myPaddle.Move(Paddle.direction.UP);
            } else
            {
                myPaddle.Move(Paddle.direction.DOWN);
            }
        }
    }

}
