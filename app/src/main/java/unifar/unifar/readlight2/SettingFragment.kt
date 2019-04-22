package unifar.unifar.readlight2

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.ads.consent.*
import kotlinx.android.synthetic.main.fragment_setting.view.*
import java.io.BufferedReader
import java.net.MalformedURLException
import java.net.URL
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import android.content.Intent
import com.android.billingclient.api.BillingClient
import com.android.vending.billing.IInAppBillingService
import android.os.IBinder
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.util.Log
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SettingFragment : Fragment() {

    private val mIsServiceConnected: Boolean = false
    private var consentInformation: ConsentInformation? = null
    private var consentForm :ConsentForm? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    private lateinit var billingClient: BillingClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        val back = view.findViewById<ImageView>(R.id.ivBack)
        back.setOnClickListener { fragmentManager?.beginTransaction()?.replace(R.id.rlMainActivityContainer, ContentFragment.newInstance())?.commit()}
        val changePersonalizedSetting = view.findViewById<Button>(R.id.changePersonalizedSetting)
        consentForm = makeConsentForm(activity!!)
        changePersonalizedSetting.setOnClickListener { consentForm?.load()}
        val licensesButton = view.findViewById<Button>(R.id.licenseButton)
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title))
        licensesButton.setOnClickListener { startActivity(Intent(activity, OssLicensesMenuActivity::class.java)) }

        billingClient = BillingClient.newBuilder(requireContext()).setListener { responseCode, purchases ->  }.build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The BillingClient is ready. You can query purchases here.
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Handler().postDelayed(
                        { billingClient.startConnection(this) }
                        , connectionRetryIntervalMill
                )
            }
        })

        val donate390Button = view.findViewById<Button>(R.id.donateButton390)
        donate390Button.setOnClickListener {
            val skuList = ArrayList<String>()
            skuList.add("adfree390")
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
            billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
                // Process the result.
                if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                    for (skuDetails in skuDetailsList) {
                        val sku = skuDetails.sku
                        val price = skuDetails.price
                        if (sku == "adfree390") {
                            Log.d(price, "billing")
                        }
                    }
                }
            }
        }

            return view
        }

    private fun makeConsentForm(context: Context): ConsentForm {
        val adManager = PersonalizedAdManager(MyApplication.getInstance())
        var privacyUrl: URL? = null
        try {
            // TODO 自分のアプリのプライバシーポリシー表示用URLをセットする
            privacyUrl = URL("https://play.google.com/store/apps/details?id=unifar.unifar.readlight2")
        } catch (e: MalformedURLException) {
            // TODO URLが異常だったらToastなどで異常を通知して問い合わせてもらうなど適当な処理を入れる
            e.printStackTrace()
        }

        val builder = ConsentForm.Builder(context, privacyUrl)
                .withPersonalizedAdsOption().withNonPersonalizedAdsOption()
        return builder.withListener(object : ConsentFormListener() {
            override fun onConsentFormLoaded() {
                // ロードが完了したらフォームを表示
                consentForm?.show()
            }

            override fun onConsentFormOpened() {
                // Consent form was displayed.
            }

            override fun onConsentFormClosed(consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?) {
                // ユーザがオプションを選択してフォームを閉じたときに発生、ここでconsentStatusをチェックする

                when (consentStatus) {
                // 同意フォームのクローズ時にSharedPreferencesに情報を保存する
                    ConsentStatus.PERSONALIZED -> adManager.updatePersonalized() // SharedPreferencesに情報を保存
                    ConsentStatus.NON_PERSONALIZED -> adManager.updateNonPersonalized() // SharedPreferencesに情報を保存
                    ConsentStatus.UNKNOWN -> {
                    }
                    else -> {
                    }
                }// 同意が得られなかったのでアプリを終了
                // TODO userPrefersAdFreeがtrueの場合はユーザが有料版アプリのオプションでOKを選択したことになるので、そちらに誘導する (今回は有料版アプリがないので無し)
            }
        }).build()
    }
/*
    private fun executeServiceRequest(runnable: Runnable) {
        if (mIsServiceConnected) {
            runnable.run()
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable)
        }
    }
*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SettingFragment.
         */
        @JvmStatic
        fun newInstance() =
                SettingFragment().apply {
                    arguments = Bundle().apply {
                    }
                    consentInformation = ConsentInformation.getInstance(MyApplication.getInstance())
                    ConsentInformation.getInstance(activity).addTestDevice("71FDD2458B24F37418B39566411942D2")
                    ConsentInformation.getInstance(activity).debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA
                }

        const val connectionRetryIntervalMill = 3000L
    }

}
