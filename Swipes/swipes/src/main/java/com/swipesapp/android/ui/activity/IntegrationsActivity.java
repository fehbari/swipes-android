package com.swipesapp.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.evernote.client.android.EvernoteSession;
import com.swipesapp.android.R;
import com.swipesapp.android.evernote.EvernoteIntegration;
import com.swipesapp.android.ui.view.SwipesDialog;
import com.swipesapp.android.util.ThemeUtils;

public class IntegrationsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getThemeResource(this));
        setContentView(R.layout.activity_integrations);

        getFragmentManager().beginTransaction().replace(R.id.integrations_content,
                new IntegrationsFragment()).commit();

        getWindow().getDecorView().setBackgroundColor(ThemeUtils.getBackgroundColor(this));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EvernoteSession.REQUEST_CODE_OAUTH:
                if (resultCode == Activity.RESULT_OK) {
                    // Refresh UI after Evernote login.
                    recreate();
                }
                break;
        }
    }

    public static class IntegrationsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.integrations);

            Preference evernoteLink = findPreference("evernote_link");
            evernoteLink.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    // Link Evernote account.
                    EvernoteIntegration.getInstance().authenticateInContext(getActivity());
                    // TODO: Find out if linking is done and reload screen.
                    return true;
                }
            });

            Preference evernoteUnlink = findPreference("evernote_unlink");
            evernoteUnlink.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    // Show confirmation dialog.
                    new SwipesDialog.Builder(getActivity())
                            .title(R.string.evernote_unlink_dialog_title)
                            .content(R.string.evernote_unlink_dialog_message)
                            .positiveText(R.string.evernote_unlink_dialog_yes)
                            .negativeText(R.string.evernote_unlink_dialog_no)
                            .actionsColorRes(R.color.neutral_accent)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    // Unlink Evernote account.
                                    EvernoteIntegration.getInstance().logoutInContext(getActivity());

                                    // Reload activity.
                                    getActivity().recreate();
                                }
                            })
                            .show();

                    return true;
                }
            });

            Preference evernoteOpenImporter = findPreference("evernote_open_importer");
            evernoteOpenImporter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // TODO: Open importer.
                    return true;
                }
            });

            Preference evernoteLearnMore = findPreference("evernote_learn_more");
            evernoteLearnMore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Show guide.
                    Intent intent = new Intent(getActivity(), EvernoteLearnActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference evernoteSyncDevice = findPreference("evernote_sync_device");
            Preference evernoteAutoImport = findPreference("evernote_auto_import");
            Preference evernoteSyncPersonal = findPreference("evernote_sync_personal");
            Preference evernoteSyncBusiness = findPreference("evernote_sync_business");

            if (EvernoteIntegration.getInstance().isAuthenticated()) {
                // Hide Evernote link button.
                getPreferenceScreen().removePreference(evernoteLink);
            } else {
                // Hide all other Evernote preferences.
                getPreferenceScreen().removePreference(evernoteUnlink);
                getPreferenceScreen().removePreference(evernoteSyncDevice);
                getPreferenceScreen().removePreference(evernoteAutoImport);
                getPreferenceScreen().removePreference(evernoteSyncPersonal);
                getPreferenceScreen().removePreference(evernoteSyncBusiness);
                getPreferenceScreen().removePreference(evernoteOpenImporter);
            }

            // TODO: Remove this in the future. These settings are only temporarily disabled.
            getPreferenceScreen().removePreference(evernoteSyncPersonal);
            getPreferenceScreen().removePreference(evernoteSyncBusiness);
            getPreferenceScreen().removePreference(evernoteOpenImporter);
        }

    }
}
