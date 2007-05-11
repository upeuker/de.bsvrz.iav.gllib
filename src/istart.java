// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 11.05.2007 11:17:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   istart.java

import java.applet.Applet;
import java.awt.*;

public class istart extends Applet
{

    public void init()
    {
        setLayout(new BorderLayout());
        add("Center", start);
        resize(50, 20);
        show();
    }

    public double readParameter()
    {
        double d = 1.2D;
        try
        {
            if(getParameter("zoom") != null)
                d = Double.valueOf(getParameter("zoom")).doubleValue();
        }
        catch(NumberFormatException _ex)
        {
            d = 1.2D;
        }
        return d;
    }

    public boolean action(Event event, Object obj)
    {
        if(event.target.equals(start))
        {
            if(ipax != null && ipax.isShowing())
            {
                start.setLabel("Start");
                ipax.quit();
                ipax.dispose();
            } else
            {
                start.setLabel("Stop");
                double d = readParameter();
                ipax = new IpaxFrame(d, start);
                ipax.resize(700, 400);
                ipax.show();
            }
        } else
        {
            return super.action(event, obj);
        }
        return true;
    }

    public istart()
    {
        start = new Button("Start");
    }

    IpaxFrame ipax;
    Button start;
}