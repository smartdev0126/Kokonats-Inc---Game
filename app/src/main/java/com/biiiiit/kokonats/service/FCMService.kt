package com.biiiiit.kokonats.service

import androidx.lifecycle.ViewModel
import com.biiiiit.kokonats.ui.user.vm.UserInfoViewModel
import com.biiiiit.lib_base.BaseApp
import com.google.firebase.messaging.FirebaseMessagingService

class FCMService : FirebaseMessagingService() {

    private val userVM: UserInfoViewModel by lazy {
        getAppViewModel(UserInfoViewModel::class.java)
    }

    override fun onNewToken(token: String) {
        userVM.registerDeviceToken(token = token)
    }

    fun <AV : ViewModel> getAppViewModel(clazz: Class<AV>): AV {
        return BaseApp.app.getAppViewModelProvider().get(clazz)
    }
}