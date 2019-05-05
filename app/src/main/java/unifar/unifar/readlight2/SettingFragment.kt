package unifar.unifar.readlight2

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import com.android.billingclient.api.*
import com.google.ads.consent.*
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SettingFragment : Fragment(), PurchasesUpdatedListener, SendNameFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
        back.setOnClickListener { fragmentManager?.beginTransaction()?.replace(R.id.mainActivityContainer, ContentFragment.newInstance())?.commit() }
        val changePersonalizedSetting = view.findViewById<Button>(R.id.changePersonalizedSetting)
        consentForm = makeConsentForm(activity!!)
        changePersonalizedSetting.setOnClickListener { consentForm?.load() }
        val licensesButton = view.findViewById<Button>(R.id.licenseButton)
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title))
        licensesButton.setOnClickListener { startActivity(Intent(activity, OssLicensesMenuActivity::class.java)) }


        val donate390Button = view.findViewById<Button>(R.id.donateButton390)
        val donate990Button = view.findViewById<Button>(R.id.donateButton990)
        val donate990ButtonDummy = view.findViewById<Button>(R.id.donateButton990dummy)
        val donate2990Button = view.findViewById<Button>(R.id.donateButton2990)
        billingClient = BillingClient.newBuilder(requireContext()).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    val skuList = listOf(AD_FREE_390, SUPPORTER_EDITION_990, SUPPORTER_EDITION_DUMMY_990, GOLD_SUPPORTER_EDITION_2990)
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
                        // Process the result.

                        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                            Log.d("billing", responseCode.toString())
                            Log.d("billing", skuDetailsList.toString())
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

        val normalNameListView = view.findViewById<ListView>(R.id.supporterNameListView)
        val goldNameListView = view.findViewById<ListView>(R.id.goldSupporterNameListView)
        val db = FirebaseFirestore.getInstance()
        //ノーマルサポーターの表示
        GlobalScope.launch {
            val arrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.normal_text_view)
            db.collection("normalSupporters")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result?.let {
                                val normalSupporterNames = it.documents.map { documentSnapshot -> documentSnapshot.data?.get("name") }.toMutableList()
                                for (normalSupporterName in normalSupporterNames) {
                                    arrayAdapter.add(normalSupporterName as String?)
                                }
                                normalNameListView.adapter = arrayAdapter
                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.exception); }
                    }
        }
        //ゴールドサポーターの表示
        GlobalScope.launch {
            val arrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.golden_text_view)
            db.collection("goldSupporters")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result?.let {
                                val goldSupporterNames = it.documents.map { documentSnapshot -> documentSnapshot.data?.get("name") }.toMutableList()
                                for (goldSupporterName in goldSupporterNames) {
                                    arrayAdapter.add(goldSupporterName as String?)
                                }
                                goldNameListView.adapter = arrayAdapter
                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.exception); }
                    }
        }

        val privacyPolicyButton = view.findViewById<Button>(R.id.privacyPolicyButton)
        privacyPolicyButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.mainActivityContainer, PrivacyPolicyFragment.newInstance()).commit()
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

    override fun onPurchasesUpdated(@BillingClient.BillingResponse responseCode: Int, purchases: List<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (purchase in purchases) {
                when (purchase.sku) {
                    AD_FREE_390 -> {
                        activity?.getSharedPreferences("Settings", MODE_PRIVATE)?.edit()?.putBoolean("isAdfree", true)?.apply()
                    }
                    SUPPORTER_EDITION_990 -> {
                        activity?.getSharedPreferences("Settings", MODE_PRIVATE)?.edit()?.putBoolean("isAdfree", true)?.apply()
                        fragmentManager?.beginTransaction()?.replace(R.id.mainActivityContainer, SendNameFragment.newInstance(false))?.commit()
                    }
                    SUPPORTER_EDITION_DUMMY_990 -> {
                        fragmentManager?.beginTransaction()?.replace(R.id.mainActivityContainer, SendNameFragment.newInstance(false))?.commit()
                    }
                    GOLD_SUPPORTER_EDITION_2990 -> {
                        activity?.getSharedPreferences("Settings", MODE_PRIVATE)?.edit()?.putBoolean("isAdfree", true)?.apply()
                        fragmentManager?.beginTransaction()?.replace(R.id.mainActivityContainer, SendNameFragment.newInstance(true))?.commit()
                    }
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
