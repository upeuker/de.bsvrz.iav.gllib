// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 11.05.2007 11:20:15
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   psstart.java

import java.awt.*;

class MsgBox extends Dialog
{

    public MsgBox(Frame frame, Button button, String as[], int i, boolean flag)
    {
        super(frame, as[0], flag);
        but = button;
        int j = as.length - 1;
        int k = getFontMetrics(getFont()).stringWidth(as[1]);
        int l = getFontMetrics(getFont()).getHeight();
        Panel panel = new Panel();
        panel.setLayout(new GridLayout(j, 1));
        Label alabel[] = new Label[j];
        for(int i1 = 0; i1 < j; i1++)
        {
            alabel[i1] = new Label();
            alabel[i1].setText(as[i1 + 1]);
            alabel[i1].setAlignment(i);
            panel.add(alabel[i1]);
            if(getFontMetrics(getFont()).stringWidth(alabel[i1].getText()) > k)
                k = getFontMetrics(getFont()).stringWidth(alabel[i1].getText());
        }

        resize(k + 50, (5 + j) * l);
        add("Center", panel);
        Panel panel1 = new Panel();
        panel1.add(new Button(" OK "));
        add("South", panel1);
    }

    public boolean handleEvent(Event event)
    {
        if(event.id == 201 && event.target == this || event.id == 1001 && (event.target instanceof Button) && ((Button)event.target).getLabel().equals(" OK "))
        {
            but.enable();
            dispose();
            return true;
        } else
        {
            return super.handleEvent(event);
        }
    }

    Button but;
}