/******************************************************************************
 *                                                                            *
 * Copyright (C) 2021 by nekohasekai <sekai@neko.services>                    *
 * Copyright (C) 2021 by Max Lv <max.c.lv@gmail.com>                          *
 * Copyright (C) 2021 by Mygod Studio <contact-shadowsocks-android@mygod.be>  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                       *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                            *
 ******************************************************************************/

package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.EditTextPreference
import com.takisoft.preferencex.PreferenceFragmentCompat
import com.takisoft.preferencex.SimpleMenuPreference
import io.nekohasekai.sagernet.Key
import io.nekohasekai.sagernet.R
import io.nekohasekai.sagernet.database.DataStore
import io.nekohasekai.sagernet.database.preference.EditTextPreferenceModifiers
import io.nekohasekai.sagernet.fmt.trojan.TrojanBean
import io.nekohasekai.sagernet.ktx.app

class TrojanSettingsActivity : ProfileSettingsActivity<TrojanBean>() {

    override fun createEntity() = TrojanBean()

    override fun init() {
        TrojanBean().apply { initDefaultValues() }.init()
    }

    override fun TrojanBean.init() {
        DataStore.profileName = name
        DataStore.serverAddress = serverAddress
        DataStore.serverPort = serverPort
        DataStore.serverPassword = password
        DataStore.serverSecurity = security
        DataStore.serverSNI = sni
        DataStore.serverALPN = alpn
        DataStore.serverFlow = flow
    }

    override fun TrojanBean.serialize() {
        name = DataStore.profileName
        serverAddress = DataStore.serverAddress
        serverPort = DataStore.serverPort
        password = DataStore.serverPassword
        security = DataStore.serverSecurity
        sni = DataStore.serverSNI
        alpn = DataStore.serverALPN
        flow = DataStore.serverFlow
    }

    lateinit var security: SimpleMenuPreference
    lateinit var tlsSni: EditTextPreference
    lateinit var tlsAlpn: EditTextPreference
    lateinit var xtlsFlow: SimpleMenuPreference

    override fun PreferenceFragmentCompat.createPreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        addPreferencesFromResource(R.xml.trojan_preferences)
        findPreference<EditTextPreference>(Key.SERVER_PORT)!!.apply {
            setOnBindEditTextListener(EditTextPreferenceModifiers.Port)
        }
        findPreference<EditTextPreference>(Key.SERVER_PASSWORD)!!.apply {
            summaryProvider = PasswordSummaryProvider
        }

        security = findPreference(Key.SERVER_SECURITY)!!
        tlsSni = findPreference(Key.SERVER_SNI)!!
        tlsAlpn = findPreference(Key.SERVER_ALPN)!!
        xtlsFlow = findPreference(Key.SERVER_FLOW)!!

        updateTle(security.value)
        security.setOnPreferenceChangeListener { _, newValue ->
            updateTle(newValue as String)
            true
        }
    }

    val xtlsFlowValue = app.resources.getStringArray(R.array.xtls_flow_value)

    fun updateTle(tle: String) {
        when (tle) {
            "tls" -> {
                xtlsFlow.isVisible = false
            }
            "xtls" -> {
                xtlsFlow.isVisible = true

                if (DataStore.serverFlow !in xtlsFlowValue) {
                    xtlsFlow.value = xtlsFlowValue[0]
                } else {
                    xtlsFlow.value = DataStore.serverFlow
                }
            }
        }
    }

}