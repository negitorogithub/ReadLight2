package unifar.unifar.readlight2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.google.ads.consent.*;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class MainActivity extends AppCompatActivity implements SendNameFragment.OnFragmentInteractionListener {

    public static InterstitialAd InterstitialAd;
    private ConsentForm consentForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PersonalizedAdManager adManager = new PersonalizedAdManager(getApplicationContext());
        setContentView(R.layout.activity_main);
        ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());
        ConsentInformation.getInstance(this).addTestDevice("71FDD2458B24F37418B39566411942D2");
        ConsentInformation.getInstance(this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        String[] publisherIds = {"pub-6418178360564076"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                // ユーザーのステータスを取得できた場合はユーザーの居住地をチェックする
                if(ConsentInformation.getInstance(getApplicationContext()).isRequestLocationInEeaOrUnknown()) {
                    // 居住地判定がtrue(=EU圏内もしくは不明)であれば、ConsentStatusをチェックする
                    // ※居住地判定がfalse(=EU圏外)であれば、今まで通りAdMobのSDKに広告のリクエストを送ってOKなので、既存処理のままとする
                    switch (consentStatus){
                        case PERSONALIZED:
                        case NON_PERSONALIZED:
                            break;
                        case UNKNOWN:
                        default:
                            // 同意情報をユーザから取得する必要があるので、Google標準の同意書を表示する
                            Log.d("rl2", "consentFormLoaded");
                            consentForm = makeConsentForm(MainActivity.this); // 後述(同意取得のフォームを表示する)
                            consentForm.load();
                            break;
                    }
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });


        if (getSupportFragmentManager().findFragmentByTag("contentFragment") == null) {
            Fragment contentFragment = ContentFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.mainActivityContainer,contentFragment,"contentFragment").commit();
        }
        Bundle extras = new Bundle();
        extras.putString("npa", "1");


        InterstitialAd = new InterstitialAd(this);
        InterstitialAd.setAdUnitId("ca-app-pub-6418178360564076/7230200212");
        if (adManager.getIsPersonalized()){
            InterstitialAd.loadAd(new AdRequest.Builder().build());
        }else {
            InterstitialAd.loadAd(new AdRequest
                    .Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build());
        }
        InterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                InterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        AppRate.with(this)
                .setInstallDays(5) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {

                    }
                })
                .monitor();
        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);



    }

    private ConsentForm makeConsentForm(Context context){
        final PersonalizedAdManager adManager = new PersonalizedAdManager(MyApplication.getInstance());
        URL privacyUrl = null;
        try {
            // TODO 自分のアプリのプライバシーポリシー表示用URLをセットする
            privacyUrl = new URL("https://play.google.com/store/apps/details?id=unifar.unifar.readlight2");
        } catch (MalformedURLException e) {
            // TODO URLが異常だったらToastなどで異常を通知して問い合わせてもらうなど適当な処理を入れる
            e.printStackTrace();
        }
        ConsentForm.Builder builder = new ConsentForm.Builder(context, privacyUrl)
        .withPersonalizedAdsOption().withNonPersonalizedAdsOption();
        return builder.withListener(new ConsentFormListener() {
            @Override
            public void onConsentFormLoaded() {
                // ロードが完了したらフォームを表示
                consentForm.show();
            }

            @Override
            public void onConsentFormOpened() {
                // Consent form was displayed.
            }

            @Override
            public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                // ユーザがオプションを選択してフォームを閉じたときに発生、ここでconsentStatusをチェックする

                switch (consentStatus) {
                    // 同意フォームのクローズ時にSharedPreferencesに情報を保存する
                    case PERSONALIZED:
                        adManager.updatePersonalized(); // SharedPreferencesに情報を保存
                        break;
                    case NON_PERSONALIZED:
                        adManager.updateNonPersonalized(); // SharedPreferencesに情報を保存
                        break;
                    case UNKNOWN:
                    default:
                        // 同意が得られなかったのでアプリを終了
                        break;
                }
            }
        }).build();
    }


    @Override
    public void onFragmentInteraction(@NotNull Uri uri) {

    }
}
