package hawaiiappbuilders.omniversapp.secure;

import static android.security.keystore.KeyProperties.BLOCK_MODE_GCM;
import static android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE;
import static android.security.keystore.KeyProperties.PURPOSE_DECRYPT;
import static android.security.keystore.KeyProperties.PURPOSE_ENCRYPT;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;

public class S {

    public static ESP getEncryptedSharedPreference(Context context) {
        return new ESP(context);
    }

    public static EF getEncryptedFile(Context context) {
        return new EF(context);
    }

    public String getOrCreateMasterKey() {
        try {
            return MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getOrCreateAdvMasterKey() {
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder("master_key", PURPOSE_ENCRYPT | PURPOSE_DECRYPT);
        builder.setBlockModes(BLOCK_MODE_GCM);
        builder.setEncryptionPaddings(ENCRYPTION_PADDING_NONE);
        builder.setKeySize(256);
        builder.setUserAuthenticationRequired(true);
        // builder.setUserAuthenticationValidityDurationSeconds(30);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            builder.setUnlockedDeviceRequired(true);
            builder.setIsStrongBoxBacked(true);
        }
        try {
            return MasterKeys.getOrCreate(builder.build());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void showUserPrompt(Context context) {
        BiometricPrompt.PromptInfo.Builder prompt = new BiometricPrompt.PromptInfo.Builder();
        prompt.setTitle("Unlock?");
        prompt.setDescription("Would you like to unlock this key?");
        // prompt.setDeviceCredentialAllowed(true);
        BiometricPrompt.PromptInfo promptInfo = prompt.build();

        new BiometricPrompt((FragmentActivity) context, ContextCompat.getMainExecutor(context), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

        }).authenticate(promptInfo);
    }

    public static class ESP {

        private SharedPreferences sharedPreferences;
        private SharedPreferences.Editor editor;

        Context context;

        public ESP(Context context) {
            this.context = context;
            createPrefs();
        }

        private void createPrefs() {
            try {
                String masterKeyAlias = getOrCreateAdvMasterKey();
                this.sharedPreferences = EncryptedSharedPreferences.create(
                        "secret_shared_prefs",
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
                editor = this.sharedPreferences.edit();
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void putEncryptedString(String key, String data) {
            this.editor.putString(key, data).apply();
        }

        public void getEncryptedString(String key) {
            this.sharedPreferences.getString(key, "");
        }

        public void putEncryptedInt(String key, int data) {
            this.editor.putInt(key, data).apply();
        }

        public void getEncryptedInt(String key) {
            this.sharedPreferences.getInt(key, 0);
        }
    }

    public static class EF {
        Context context;
        String filesDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OmniVers/";

        public EF(Context context) {
            this.context = context;
        }

        private EncryptedFile getEncryptedFile(File secretFile) throws GeneralSecurityException, IOException {
            String masterKey = getOrCreateAdvMasterKey();
            EncryptedFile.Builder builder = new EncryptedFile.Builder(secretFile, context.getApplicationContext(), masterKey, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB);
            // builder.setKeysetAlias("file_key");
            builder.setKeysetPrefName("secret_shared_prefs");

            // write to the encrypted file
            // FileOutputStream encryptedOutputStream = file.openFileOutput();

            // read the encrypted file
            // FileInputStream encryptedInputStream = file.openFileInput();
            return builder.build();
        }

        private void deleteIfExists(File secretFile) {
            if (secretFile.exists()) {
                secretFile.delete();
            }
        }

        private boolean exists(File secretFile) {
            return secretFile.exists();
        }

        public void writeToEncryptedFile(String filename, String data) {
            File secretFile = new File(filesDir, filename);
            deleteIfExists(secretFile);
            if (secretFile.exists()) {
                try {
                    EncryptedFile encryptedFile = getEncryptedFile(secretFile);
                    try {
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(encryptedFile.openFileOutput()));
                        bufferedWriter.write(data);
                        bufferedWriter.close();
                    } catch (Exception exception) {
                        Log.i("S", "Error writing to encrypted file");
                    }

                } catch (GeneralSecurityException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public String readFromEncryptedFile(String filename) {
            String data = "";
            File secretFile = new File(filesDir, filename);
            if (exists(secretFile)) {
                try {
                    EncryptedFile encryptedFile = getEncryptedFile(secretFile);
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(encryptedFile.openFileInput()));
                        // Condition holds true till there is character in a string
                        while ((data = in.readLine()) != null)
                            return data;
                        in.close();
                    } catch (Exception exception) {
                        Log.i("S", "Error writing to encrypted file");
                    }

                } catch (GeneralSecurityException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return data;
        }

    }


}
