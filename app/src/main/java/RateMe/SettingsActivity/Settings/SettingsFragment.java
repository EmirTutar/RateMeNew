package RateMe.SettingsActivity.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.rateme.R;

import RateMe.SettingsActivity.AboutUsActivity;
import RateMe.SettingsActivity.PermissionsActivity;
import RateMe.SettingsActivity.Profile.ProfileActivity;

/**
 * Zeigt eine Liste von Einstellungsoptionen an. Nutzer können von hier aus zu den
 * Detailseiten für Profil, Berechtigungen und Über Uns navigieren.
 */

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        ListView settingsList = view.findViewById(R.id.settings_list);
        String[] settingsOptions = {"Profile", "Permissions", "About Us"};

        //noinspection DataFlowIssue
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, settingsOptions);
        settingsList.setAdapter(adapter);

        settingsList.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getActivity(), PermissionsActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(getActivity(), AboutUsActivity.class));
                    break;
            }
        });
        return view;
    }
}
