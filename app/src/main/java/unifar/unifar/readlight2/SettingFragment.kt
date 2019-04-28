package unifar.unifar.readlight2

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.ads.consent.*
import java.net.MalformedURLException
import java.net.URL
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import android.content.Intent
import android.content.Context.MODE_PRIVATE
import android.os.Handler
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SettingFragment : Fragment(), PurchasesUpdatedListener {

    private val mIsServiceConnected: Boolean = false
    private var consentInformation: ConsentInformation? = null
    private var consentForm: ConsentForm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

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
        const val AD_FREE_390 = "adfree390"
        const val SUPPORTER_EDITION_990 = "supporter_edition990"
        const val SUPPORTER_EDITION_DUMMY_990 = "supporter_edition_dummy990"
        const val GOLD_SUPPORTER_EDITION_2990 = "gold_supporter_edition2990"

    }

    private lateinit var billingClient: BillingClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        val back = view.findViewById<ImageView>(R.id.ivBack)
        back.setOnClickListener { fragmentManager?.beginTransaction()?.replace(R.id.rlMainActivityContainer, ContentFragment.newInstance())?.commit() }
        val changePersonalizedSetting = view.findViewById<Button>(R.id.changePersonalizedSetting)
        consentForm = makeConsentForm(activity!!)
        changePersonalizedSetting.setOnClickListener { consentForm?.load() }
        val licensesButton = view.findViewById<Button>(R.id.licenseButton)
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title))
        licensesButton.setOnClickListener { startActivity(Intent(activity, OssLicensesMenuActivity::class.java)) }

        billingClient = BillingClient.newBuilder(requireContext()).setListener(this).build()
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
        GlobalScope.launch {
            awaitTest()
        }

        val donate390Button = view.findViewById<Button>(R.id.donateButton390)
        val donate990Button = view.findViewById<Button>(R.id.donateButton990)
        val donate990ButtonDummy = view.findViewById<Button>(R.id.donateButton990dummy)
        val donate2990Button = view.findViewById<Button>(R.id.donateButton2990)

        GlobalScope.launch {
            val skuList = listOf(AD_FREE_390, SUPPORTER_EDITION_990, SUPPORTER_EDITION_DUMMY_990, GOLD_SUPPORTER_EDITION_2990)
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
            billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
                // Process the result.
                if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                    skuDetailsList.forEach { skuDetails: SkuDetails ->
                        when (skuDetails.sku) {
                            AD_FREE_390 -> {
                                donate390Button.text = resources.getString(R.string.donate390, skuDetails.price)
                                donate390Button.setOnClickListener {
                                    launchPurchaseFlow(skuDetails)
                                }
                            }
                            SUPPORTER_EDITION_990 -> {
                                donate990Button.text = resources.getString(R.string.donate990, skuDetails.price)
                                donate990Button.setOnClickListener {
                                    launchPurchaseFlow(skuDetails)
                                }
                            }
                            SUPPORTER_EDITION_DUMMY_990 -> {
                                donate990ButtonDummy.text = resources.getString(R.string.donate990dummy, skuDetails.price)
                                donate990ButtonDummy.setOnClickListener {
                                    launchPurchaseFlow(skuDetails)
                                }
                            }
                            GOLD_SUPPORTER_EDITION_2990 -> {
                                donate2990Button.text = resources.getString(R.string.donate2990, skuDetails.price)
                                donate2990Button.setOnClickListener {
                                    launchPurchaseFlow(skuDetails)
                                }
                            }
                        }
                    }
                }
            }
        }
        return view
    }

    private fun queryItem(itemName: String) {
        val skuList = ArrayList<String>()
        skuList.add(itemName)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
            // Process the result.
            if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()
                    billingClient.launchBillingFlow(activity, flowParams)
                }
            }
        }
    }

    private suspend fun awaitTest() {
        val deferred = GlobalScope.async {
            delay(4000)
            return@async 10
        }
        val result = deferred.await()
        println("result = $result") // "result = 10" が出力される
    }

    private suspend fun queryItemDetails(itemNameList: List<String>): List<SkuDetails> {
        val params = SkuDetailsParams.newBuilder()
        var skuList2return = ArrayList<SkuDetails>()
        params.setSkusList(itemNameList).setType(BillingClient.SkuType.INAPP)

        return GlobalScope.async {

            billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
                // Process the result.
                if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                    skuList2return = skuDetailsList as ArrayList<SkuDetails>
                }
            }

            return@async skuList2return
        }.await()
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

    override fun onPurchasesUpdated(@BillingClient.BillingResponse responseCode: Int, purchases: List<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.sku == AD_FREE_390) {
                    activity?.getSharedPreferences("Settings", MODE_PRIVATE)?.edit()?.putBoolean("isAdfree", true)?.apply()
                }
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    private fun launchPurchaseFlow(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }


}
