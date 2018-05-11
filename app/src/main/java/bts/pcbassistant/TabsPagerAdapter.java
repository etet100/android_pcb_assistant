package bts.pcbassistant;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import bts.pcbassistant.welcome.WelcomeFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    private WelcomeFragment t1 = null;
    private WorkspaceFragment t2 = null;
    private WorkspaceFragment t3 = null;

    @Override
    public android.support.v4.app.Fragment getItem(int index)
    {
        /*
        switch (index)
        {
            case 0:
                if (t1 == null)
                    t1 = new WelcomeFragment_();
                return t1;
            case 1:
                if (t2 == null)
                    t2 = new MainFragment_();
                return t2;
            case 2:
                if (t3 == null)
                    t3 = new MainFragment_();
                return t3;
        }
        */
        return null;
    }

    @Override
    public int getCount()
    {
        return 2;
    }
}