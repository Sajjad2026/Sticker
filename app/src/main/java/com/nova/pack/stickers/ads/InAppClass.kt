package com.nova.pack.stickers.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.common.collect.ImmutableList
import com.nova.pack.stickers.R

class InAppClass {

    companion object{

        var billingClient: BillingClient? = null

        var ONE_WEEK_: String? = null
        var ONE_MONTH_: String? = null
        var THREE_MONTH_: String? = null
        var LIFETIME_: String? = null
        //discount
        var ONE_WEEK_CUT: String? = null
        var ONE_MONTH_CUT: String? = null
        var THREE_MONTH_CUT: String? = null
        var LIFETIME_CUT: String? = null

        var isPurchase = false

        var isPurchaseOneWeek = false
        var isPurchaseOneMonth = false
        var isPurchaseThreeMonthly = false
        var isPurchaseLifeTime = false

        var isBpClientReady: Boolean = false
        var contextGlobal: Context? = null

        var oneWeekDetail: ProductDetails? = null
        var oneMonthDetail: ProductDetails? = null
        var threeMonthDetail: ProductDetails? = null
        var lifetimeDetail: ProductDetails? = null
        //discount
        var oneWeekDetailCut: ProductDetails? = null
        var oneMonthDetailCut: ProductDetails? = null
        var threeMonthDetailCut: ProductDetails? = null
        var lifetimeDetailCut: ProductDetails? = null

        var billingisReady: Boolean = false
        var purchasedPending: Boolean = false


        fun handleUserPurchase(purchase: Purchase, context: Context?) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                Log.d("Sfdjhdf",purchase.products.toString())
                if (purchase.products.toString().contains(context?.getString(R.string.oneWeek)!!)) {
                    isPurchaseOneWeek = true
                    isPurchase = true
                }

                if (purchase.products.toString().contains(context?.getString(R.string.oneMonth)!!)) {
                    isPurchaseOneMonth = true
                    isPurchase = true
                }

                if (purchase.products.toString().contains(context?.getString(R.string.threeMonth)!!)) {
                    isPurchaseThreeMonthly = true
                    isPurchase = true

                }

                if (purchase.products.toString().contains(context?.getString(R.string.lifeTime)!!)) {
                    isPurchaseLifeTime = true
                    isPurchase = true
                }
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    val acknowledgePurchaseResponseListener =
                        AcknowledgePurchaseResponseListener { billingResult ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                // Handle the success of the consume operation.
//                                onPurchasedItemDialog(context)
                            }
                        }
                    billingClient!!.acknowledgePurchase(
                        acknowledgePurchaseParams,
                        acknowledgePurchaseResponseListener
                    )
                } else {
                    Log.d("TAG_1", "handlePurchase: else ")
//                    onPurchasedItemDialog(context)
                }
            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                purchasedPending = true
            }
        }

        var purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, list ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                    for (purchase in list) {
                        handleUserPurchase(
                            purchase,
                            contextGlobal
                        )
                    }
                } else {
                    try {
                        if (!billingResult.debugMessage.isEmpty()) {
                            Toast.makeText(
                                contextGlobal,
                                billingResult.debugMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        fun setupBillingBlient(context: Context) {
            try {
                contextGlobal = context

                ONE_WEEK_ = context.getString(R.string.oneWeek)
                ONE_MONTH_ = context.getString(R.string.oneMonth)
                THREE_MONTH_ = context.getString(R.string.threeMonth)
                LIFETIME_ = context.getString(R.string.lifeTime)

                ONE_WEEK_CUT = context.getString(R.string.oneWeekCut)
                ONE_MONTH_CUT = context.getString(R.string.oneMonthCut)
                THREE_MONTH_CUT = context.getString(R.string.threeMonthCut)
                LIFETIME_CUT = context.getString(R.string.lifeTimeCut)

                billingClient = BillingClient.newBuilder(context)
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases()
                    .build()
                if (!billingClient!!.isReady()) {
                    billingClient!!.startConnection(billingStateListener)
                    Log.d("isbilling", billingClient.toString())
                }
                Log.d("isbilling", ONE_WEEK_.toString())

            } catch (e: Exception) {
                Log.d("isbilling",e.message.toString())
            }
        }

        var billingStateListener: BillingClientStateListener =
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d("isbilling","enter")
                        Log.d("BILLING", "${billingResult.debugMessage}")
                        isBpClientReady = true
                        billingisReady = true
                        Log.d("isbilling","enter 6")
                        getOnetimePurchases()
                        Log.d("isbilling","enter 3")
                        getSubscriptionPurchases()
                        Log.d("isbilling","enter 4")
                        querySubscriptions()
                        Log.d("isbilling","enter 5")
                        queryProducts()
                        Log.d("isbilling","enter 2")
                    }else{
                        Log.d("isbilling","null")
                    }
                    // test in App for ads by un-commenting below line
//                    isPurchase = true
                }

                override fun onBillingServiceDisconnected() {
                    //Toast.makeText(contextGlobal, "disconnected", Toast.LENGTH_SHORT).show();
                }
            }

        private fun queryProducts() {

            val productList = ImmutableList.of(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(LIFETIME_.toString())
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
                ,QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(LIFETIME_CUT.toString())
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
//                QueryProductDetailsParams.Product.newBuilder()
//                    .setProductId(LIFETIME_DISC.toString())
//                    .setProductType(BillingClient.ProductType.INAPP)
//                    .build()
            )
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()
            billingClient!!.queryProductDetailsAsync(
                params,
                ProductDetailsResponseListener { billingResult: BillingResult, prodDetailsList: List<ProductDetails> ->
                    Log.d("prodDetailsList", "${prodDetailsList.size}")
                    if (prodDetailsList == null) {
                        Log.d("prodDetailsList", "null if")
                    } else {
                        Log.d("productList except", "here" + prodDetailsList!!.size.toString())
                        if (prodDetailsList.size>0) {
                            for (productDetails in prodDetailsList) {
                                if (productDetails.productId == LIFETIME_) {
                                    lifetimeDetail = productDetails
                                }
                                if (productDetails.productId == LIFETIME_CUT) {
                                    lifetimeDetailCut = productDetails
                                }
//                                else if (productDetails.productId == LIFETIME_DISC){
//                                    LIFETIME_disc = productDetails
//                                }
                            }
                        } else {
                            lifetimeDetail = null
                            lifetimeDetailCut = null
//                            LIFETIME_disc = null
                        }
                    }
                }
            )
        }

        fun launch_One_Time_Purchase_billing_flow(activity: Activity, productDetails: ProductDetails) {
            Log.d("launch", productDetails.description)
            assert(productDetails.oneTimePurchaseOfferDetails != null)
            val productDetailsParamsList = ImmutableList.of(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val responseCode: Int =
                billingClient!!.launchBillingFlow(activity, billingFlowParams)
                    .getResponseCode()
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                setupBillingBlient(contextGlobal as Context)
            } else if (responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                Toast.makeText(activity, "Billing Unavailable!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }

        private fun getOnetimePurchases() {
            if (!isPurchase) {
                billingClient!!.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                    PurchasesResponseListener { billingResult: BillingResult, list: List<Purchase> ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            for (purchase in list) {
                                Log.d("Pending", "${purchase.products}")
                                //   if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged())
                                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                    Log.d("Pending", "true true")
                                    verifySubPurchase(purchase, contextGlobal!!)
                                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                                    purchasedPending = true

                                }
                            }
                        }
                    }
                )
            }
        }

        fun querySubscriptions() {
            Log.d("query_subscriptions", "query_products: here")
            val productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(ONE_WEEK_.toString())
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(ONE_MONTH_.toString())
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),  //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(THREE_MONTH_.toString())
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                //DISCOUNT
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(ONE_WEEK_CUT.toString())
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(ONE_MONTH_CUT.toString())
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),  //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(THREE_MONTH_CUT.toString())
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
                //PRODUCT 4

            )

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()
            billingClient!!.queryProductDetailsAsync(
                params,
                ProductDetailsResponseListener { billingResult: BillingResult?, prodDetailsList: List<ProductDetails>? ->
                    // Process the result
                    try {
                        if (prodDetailsList == null) {
                            Log.d("productList", "null if")
                        } else {
                            if (prodDetailsList.size>0) {
                                Log.d("ajdhd","null k")

                                for (productDetails in prodDetailsList) {
                                    if (productDetails.productId == ONE_WEEK_) {
                                        oneWeekDetail = productDetails
                                    } else if (productDetails.productId == ONE_MONTH_) {
                                        oneMonthDetail = productDetails
                                    } else if (productDetails.productId == THREE_MONTH_) {
                                        threeMonthDetail = productDetails
                                    }

                                    if (productDetails.productId == ONE_WEEK_CUT) {
                                        oneWeekDetailCut = productDetails
                                    } else if (productDetails.productId == ONE_MONTH_CUT) {
                                        oneMonthDetailCut = productDetails
                                    } else if (productDetails.productId == THREE_MONTH_CUT) {
                                        threeMonthDetailCut = productDetails
                                    }
                                }
                            } else {
                                Log.d("ajdhd","null k")
                                oneWeekDetail = null
                                oneMonthDetail = null
                                threeMonthDetail = null
                                oneWeekDetailCut = null
                                oneMonthDetailCut = null
                                threeMonthDetailCut = null
                            }
                        }
                    } catch (w: java.lang.Exception) {
                    }
                }
            )
        }

        fun verifySubPurchase(purchases: Purchase,context: Context) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.purchaseToken)
                .build()
            billingClient!!.acknowledgePurchase(acknowledgePurchaseParams,
                AcknowledgePurchaseResponseListener { billingResult: BillingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        //user prefs to set premium
//                    Log.d("TAG_1", "verifySubPurchase: " + purchases.products)

                        if (purchases.products.toString().contains(context?.getString(R.string.oneWeek)!!)) {
                            isPurchaseOneWeek = true
                            isPurchase = true
                        }

                        if (purchases.products.toString().contains(context?.getString(R.string.oneMonth)!!)) {
                            isPurchaseOneMonth = true
                            isPurchase = true
                        }

                        if (purchases.products.toString().contains(context?.getString(R.string.threeMonth)!!)) {
                            isPurchaseThreeMonthly = true
                            isPurchase = true

                        }

                        if (purchases.products.toString().contains(context?.getString(R.string.lifeTime)!!)) {
                            isPurchaseLifeTime = true
                            isPurchase = true
                        }
                    }
                })
        }

        fun getSubscriptionPurchases() {

            // List<Purchase> purchasesInApp, purchasesSub;
            if (!isPurchase) {
                billingClient!!.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                    PurchasesResponseListener { billingResult: BillingResult, list: List<Purchase> ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            for (purchase in list) {
                                Log.d(
                                    "TAG_1",
                                    "get_subscription_purchases: " + purchase.purchaseState
                                )

                                //   if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged())
                                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                    verifySubPurchase(purchase, contextGlobal!!)
                                    Log.d(
                                        "TAG_1",
                                        "get_subscription_purchases: if" + purchase.purchaseState
                                    )
                                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                                    purchasedPending = true
                                } else {
                                    Log.d(
                                        "TAG_1",
                                        "get_subscription_purchases: else " + purchase.purchaseState
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        fun launch_Subscription_billing_flow(activity: Activity, productDetails: ProductDetails) {
            Log.d("asyhjad","1")
            assert(productDetails.subscriptionOfferDetails != null)
            val productDetailsParamsList = ImmutableList.of(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                    .build()
            )
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val responseCode: Int =
                billingClient!!.launchBillingFlow(activity, billingFlowParams)
                    .getResponseCode()
            Log.d("asyhjad","2")
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d("code", "launch_Subscription_billing_flow: $responseCode")
                getSubscriptionPurchases()
                setupBillingBlient(contextGlobal as Context)
            } else if (responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                Toast.makeText(activity, "Billing Unavailable!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}