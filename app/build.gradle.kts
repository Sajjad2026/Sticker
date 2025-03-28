plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("kotlinx-serialization")
    id("com.google.firebase.crashlytics")
}
var contentProviderAuthority = ""
android {
    bundle {
        language {
            enableSplit = false
        }
    }
    namespace = "com.nova.pack.stickers"
    compileSdk = 34

    defaultConfig {
        manifestPlaceholders += mapOf()
        applicationId = "com.nova.pack.stickers"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        contentProviderAuthority = "$applicationId.provider.StickerContentProvider"
        manifestPlaceholders["contentProviderAuthority"] = contentProviderAuthority
        buildConfigField("String", "CONTENT_PROVIDER_AUTHORITY", "\"${contentProviderAuthority}\"")

        ndk {
            abiFilters += setOf("armeabi-v7a", "arm64-v8a")
        }
        signingConfig = signingConfigs.getByName("debug")
    }

    buildFeatures.viewBinding = true
    android.buildFeatures.buildConfig = true
    buildTypes {
        release {
            isMinifyEnabled =  true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled =  false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    // Retrofit + GSON
//    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    //Room Database
    kapt("androidx.room:room-compiler:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    annotationProcessor ("androidx.room:room-compiler:2.6.0")
    implementation ("androidx.room:room-runtime:2.6.0")
    implementation("com.google.firebase:firebase-crashlytics:19.0.3")
//    implementation("com.google.firebase:firebase-inappmessaging-display:20.4.0")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")
    implementation("androidx.lifecycle:lifecycle-process:2.8.1")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation("com.hbb20:ccp:2.7.0")

    // To use Kotlin annotation processing tool (kapt)

    // optional - Kotlin Extensions and Coroutines support for Room

    //Hilt Dependency
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    // JCHUCOMPONENTS LIBRARY ----------------------------------------------------------------------
//    implementation("com.github.jeluchu.jchucomponents:jchucomponents-core:1.0.2")
    implementation("com.github.jeluchu.jchucomponents:jchucomponents-ktx:1.0.2")
    implementation("com.google.code.gson:gson:2.10.1")

    // MULTIMEDIA ----------------------------------------------------------------------------------
//    implementation("io.coil-kt:coil:2.2.2")
//    implementation("io.coil-kt:coil-gif:2.2.2")

    //ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    //ImageCrop
    implementation("com.vanniktech:android-image-cropper:4.5.0")

    //Add Text And Stickers
    implementation("com.burhanrashid52:photoeditor:3.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    //Bg Remover
    implementation("com.github.duanhong169:checkerboarddrawable:1.0.2")
    implementation("com.github.GhayasAhmad:auto-background-remover:1.0.3")
    //implementation("com.google.mlkit:segmentation-selfie:16.0.0-beta5")
//    implementation("com.huawei.hms:ml-computer-vision-segmentation:3.7.0.302")
//    implementation("com.huawei.hms:ml-computer-vision-image-segmentation-body-model:3.7.0.302")

    implementation("io.github.farimarwat:zerocrash:1.6")

    //Color Picker
    implementation("com.github.skydoves:colorpickerview:2.3.0")

    //Firebase Remote Config
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-analytics")

    //Admob Ads
    implementation("com.google.android.gms:play-services-ads:23.6.0")

    //Applovin
    implementation("com.applovin:applovin-sdk:13.0.0")
    implementation("com.google.ads.mediation:facebook:6.18.0.0")
//    implementation("com.applovin.mediation:facebook-adapter:6.11.0.5")

    //Dynamic Screen Size
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    implementation ("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    val billing_version = "7.0.0"
    implementation("com.android.billingclient:billing:$billing_version")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

//    implementation("com.github.redevrx:android_video_trimmer:1.0.2")
//    implementation("com.arthenica:mobile-ffmpeg-full-gpl:4.4")
}
