import org.gradle.api.JavaVersion

const val appId = "com.lm.snake"
const val appVersion = "1.0"
const val testRunner = "androidx.test.runner.AndroidJUnitRunner"
const val proGName = "proguard-android-optimize.txt"
const val proGRules = "proguard-rules.pro"
val javaVersion = JavaVersion.VERSION_11
const val jvm = "11"
const val res = "/META-INF/{AL2.0,LGPL2.1}"
val argsList = listOf("-Xjvm-default=all", "-opt-in=kotlin.RequiresOptIn")

