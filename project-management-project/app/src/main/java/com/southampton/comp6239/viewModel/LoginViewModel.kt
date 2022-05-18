package com.southampton.comp6239.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.southampton.comp6239.bean.User

class LoginViewModel : ViewModel(){

    var user : MutableLiveData<User> = MutableLiveData(User())


}