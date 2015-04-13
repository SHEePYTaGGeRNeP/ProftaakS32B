/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shared;

import Server.Server;
import java.awt.Color;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jelle
 */
public class BallTest
{

    private Ball b;
    private Game testGame;
    private CPU cpu;
    private User user;
    private TVector2 position;
    private TVector2 velocity;
    private TVector2 size;
    private Paddle p;
    private Server server;

    @Before
    public void setUp()
    {
        testGame = new Game(1, 180, false);
        server = new Server();
        cpu = new CPU("Bot", (byte) 5, testGame);
        user = new User("Jelle123", "wachtwoord123", "jelle@gmail.com", server);
        position = new TVector2(50, 30);
        velocity = new TVector2(15, 40);
        size = new TVector2(5, 5);
        p = new Paddle(33, position, velocity, size, cpu, Paddle.windowLocation.NORTH, Color.BLACK);
    }

    @Test
    public void testBall()
    {
        /**
         * Creates a new ball object with a paddle, position, velocity and size.
         */
        try
        {
            b = new Ball(p, position, velocity, size);
            assertEquals(p, b.getLastPaddleTouched());
            assertEquals(position, b.getPosition());
            assertEquals(velocity, b.getVelocity());
            assertEquals(size, b.getSize());
        } catch (IllegalArgumentException exc)
        {
            fail("Constructor failure!");
        }

        // @param lastPaddleTouched The last Paddle which touched the ball.
        try
        {
            b = new Ball(null, position, velocity, size);
            fail("lastPaddleTouched cannot be null.");
        } catch (IllegalArgumentException exc)
        {

        }

        // @param position The position of a GameObject.
        try
        {
            b = new Ball(p, null, velocity, size);
            fail("Position of a GameObject cannot be null.");
        } catch (IllegalArgumentException exc)
        {

        }

        // @param velocity The velocity of a GameObject.
        try
        {
            b = new Ball(p, position, null, size);
            fail("Velocity of a GameObject cannot be null.");
        } catch (IllegalArgumentException exc)
        {

        }

        // @param size The size of a GameObject.
        try
        {
            b = new Ball(p, position, velocity, null);
            fail("Size of a GameObject cannot be null.");
        } catch (IllegalArgumentException exc)
        {

        }
    }

    @Test
    public void testSetLastPaddleTouched()
    {
        b = new Ball(p, position, velocity, size);
        Paddle p2 = new Paddle(100, position, velocity, size, cpu, Paddle.windowLocation.SOUTH, Color.BLACK);
        try
        {
            b.setLastPaddleTouched(p2);
            assertEquals(p2, b.getLastPaddleTouched());
        } catch (IllegalArgumentException exc)
        {

        }
        
        try
        {
            b.setLastPaddleTouched(null);
            fail("lastPaddleTouched cannot be null.");
        } catch (IllegalArgumentException exc)
        {

        }
    }
}
