
package money.myhubble.storesdk

import org.json.JSONObject

object Key {
    const val CLIENT_ID = "clientId"
    const val CLIENT_SECRET = "clientSecret"
    const val TOKEN = "token"
    const val ENV = "env"
    const val PAGE = "page"
}

object HubbleEnv {
    const val PROD = "prod"
    const val DEV = "dev"
}

class HubblePage(
    var page: String,
    var params: Map<String, String>? = null,
) {
    companion object {
        fun fromJson(json: String): HubblePage {
            val jsonObject = JSONObject(json)
            val page = jsonObject.getString("page")
            val params = jsonObject.getJSONObject("params").toMap()
            return HubblePage(page, params)
        }

        private fun JSONObject.toMap(): Map<String, String> {
            val map = mutableMapOf<String, String>()
            this.keys().forEach {
                map[it] = this.getString(it)
            }
            return map
        }
    }

    fun toJson(): String {
        val params = JSONObject(this.params ?: emptyMap<String, String>())
        return JSONObject(mapOf("page" to this.page, "params" to params)).toString()
    }
}
