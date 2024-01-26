package tv.willow.Models

import android.util.Log
import com.willow.android.mobile.models.auth.UserModel
import org.json.JSONObject
import java.io.Serializable


class SettingsPageModel {
    val result: MutableList<SettingsItemModel> = mutableListOf()

    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONArray("result")
            for (i in 0 until resultJson.length()) {
                val resultJsonUnit = resultJson[i] as? JSONObject
                if (resultJsonUnit != null) {
                    val settingsItemModel = SettingsItemModel()
                    settingsItemModel.setData(resultJsonUnit)

                    if (settingsItemModel.action.equals("logout") && (!(UserModel.isLoggedIn()))) {
                        continue
                    }
                    result.add(settingsItemModel)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "SettingsPageModel field: " + "result")
        }
    }
}

class SettingsItemModel : Serializable {
    var title: String = ""
    var subtitle: String = ""
    var url: String = ""
    var icon: String = ""
    var action: String = ""
    var subitems: MutableList<SettingsSubItem> = mutableListOf()

    fun setData(data: JSONObject) {
        try {
            title = data.getString("title")
        } catch (e: Exception) {
            Log.e("FieldError:", "SettingsItemModel field: " + "title")
        }

        try {
            subtitle = data.getString("subtitle")
        } catch (e: Exception) {
            Log.e("FieldError:", "SettingsItemModel field: " + "subtitle")
        }

        try {
            url = data.getString("url")
        } catch (e: Exception) {
            Log.e("FieldError:", "SettingsItemModel field: " + "url")
        }

        try {
            icon = data.getString("icon")
        } catch (e: Exception) {
            Log.e("FieldError:", "SettingsItemModel field: " + "icon")
        }

        try {
            action = data.getString("action")
        } catch (e: Exception) {
            Log.e("FieldError:", "SettingsItemModel field: " + "action")
        }

        try {
            val subitemsArray = data.getJSONArray("subitems")
            for (i in 0 until subitemsArray.length()) {
                val subitemJson = subitemsArray[i] as? JSONObject
                if (subitemJson != null) {
                    val settingsSubItem = SettingsSubItem()
                    settingsSubItem.setData(subitemJson)

                    if (settingsSubItem.action.equals("request_password") && (!(UserModel.shouldShowRequestPasswordOption))) {
                        continue
                    }

                    if (settingsSubItem.action.equals("verify_email") && ((UserModel.emailVerified))) {
                        continue
                    }

                    if (settingsSubItem.action.equals("subscription_details") && ((!UserModel.isSubscribed))) {
                        continue
                    }

                    subitems.add(settingsSubItem)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "SettingsItemModel field: " + "subitems")
        }
    }
}

class SettingsSubItem : Serializable {
    var title: String = ""
    var subtitle: String = ""
    var url:String = ""
    var icon:String = ""
    var action:String = ""

    fun setData(data: JSONObject) {
        try {
            title = data.getString("title")
        } catch (e: Exception) {
            Log.e("FieldError:", "SettingsSubItem field: " + "title")
        }

        try {
            subtitle = data.getString("subtitle")
        } catch (e: Exception) { }

        try {
            url = data.getString("url")
        } catch (e: Exception) { }

        try {
            icon = data.getString("icon")
        } catch (e: Exception) { }

        try {
            action = data.getString("action")
        } catch (e: Exception) { }
    }
}