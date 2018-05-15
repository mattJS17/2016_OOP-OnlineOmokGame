import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

public class OmokPanelUI extends JPanel
{
    public static final int interval=35;
    public static final int num=19;
    public static final int sizeStone=25;
    public static final int BLACK=1;
    public static final int WHITE=-1;
    public static final int SCHEDULEBLACK=-1;
    public static final int SCHEDULEWHITE=1;
    public static final int BLACKWIN=1;
    public static final int WHITEWIN=2;

    public int[][] stone;
    public int[][] scheduleStone;

    public Graphics brush;

    public OmokPanelUI()
    {
        stone=new int[num][num];
        scheduleStone=new int[num][num];

        setBackground(new Color(139,136,3));

        setLayout(null);
    }

    public void paintComponent(Graphics page)
    {
        super.paintComponent(page);

        Graphics2D page2=(Graphics2D)page;

        brush=page;

        page.setColor(Color.black);

        for(int i=0;i<num;i++)
        {
            page.drawLine(interval+i*interval, interval, interval+i*interval, 700-interval);
            page.drawLine(700-interval, interval+i*interval, interval, interval+i*interval);
        }

        for(int i=0;i<num;i++)
        {
            for(int j=0;j<num;j++)
            {
                if(stone[i][j]==BLACK)
                {
                    page.setColor(Color.BLACK);
                    page.fillOval(interval+i*interval-sizeStone/2, interval+j*interval-sizeStone/2, sizeStone, sizeStone);
                }
                else if(stone[i][j]==WHITE)
                {
                    page.setColor(Color.WHITE);
                    page.fillOval(interval+i*interval-sizeStone/2, interval+j*interval-sizeStone/2, sizeStone, sizeStone);
                    page.setColor(Color.BLACK);
                    page.drawOval(interval+i*interval-sizeStone/2, interval+j*interval-sizeStone/2, sizeStone, sizeStone);
                }
                if(scheduleStone[i][j]==SCHEDULEBLACK)
                {
                    page.setColor(Color.BLACK);
                    page.fillOval(interval+i*interval-sizeStone/2, interval+j*interval-sizeStone/2, sizeStone, sizeStone);
                    page.setColor(Color.RED);
                    page.drawOval(interval+i*interval-sizeStone/2, interval+j*interval-sizeStone/2, sizeStone, sizeStone);
                    scheduleStone[i][j]=0;
                }
                else if(scheduleStone[i][j]==SCHEDULEWHITE)
                {
                    page.setColor(Color.WHITE);
                    page.fillOval(interval+i*interval-sizeStone/2, interval+j*interval-sizeStone/2, sizeStone, sizeStone);
                    page.setColor(Color.RED);
                    page.drawOval(interval+i*interval-sizeStone/2, interval+j*interval-sizeStone/2, sizeStone, sizeStone);
                    scheduleStone[i][j]=0;
                }
            }
        }
    }



    public void addMouseListener(MouseListener Mlistener, MouseMotionListener MMlistener) {
        this.addMouseListener(Mlistener);
        this.addMouseMotionListener(MMlistener);
    }
}