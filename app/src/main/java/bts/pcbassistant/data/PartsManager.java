package bts.pcbassistant.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by a on 2017-05-11.
 */

public class PartsManager implements Parcelable {

    Part selectedPart = null;
    List<Part> parts = null;

    public PartsManager() {
        parts = Collections.synchronizedList(
                new ArrayList<Part>()
        );
    }

    public List<Part> getParts() {
        return parts;
    }

    public boolean hasSelectedPart() {
        return (selectedPart != null);
    }

    private Part findByName(String name) {
        synchronized (parts) {
            for (Part p : parts) {
                if (p.getName().compareTo(name) == 0) {
                    return p;
                }
            }
        }
        return null;
    }

    public Part selectByName(String partName) {
        selectedPart = findByName(partName);
        return selectedPart;
    }

    public Part select(Part part) {
        selectedPart = part;
        return selectedPart;
    }

    public Part add(EagleDataSource.TYPE sourceType, Part part) {
        synchronized (parts) {
            Part p = findByName(part.getName());
            if (p == null) {
                //p = new Part();
                parts.add(part);
                return part;
            }
            return p;
        }
    }

    public Part get(String name) {
        return findByName(name);
    }

    public Part get(EagleDataSource.TYPE sourceType, String library, String name, String value) {
        synchronized (parts) {
            Part p = findByName(name);
            if (p == null) {
                return add(sourceType, new Part(
                        library,
                        name,
                        value
                ));
            } else
                return p;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
