package tw.hardy.taipeiattractions.ui.main

import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.launch
import tw.hardy.base.ui.base.BaseFactory
import tw.hardy.taipeiattractions.R
import tw.hardy.taipeiattractions.data.repository.RepositoryLocator
import tw.hardy.taipeiattractions.databinding.ActivityMainBinding
import tw.hardy.taipeiattractions.ui.base.BaseLanguageActivity
import tw.hardy.taipeiattractions.utils.LanguageUtil
import java.util.Locale


class MainActivity : BaseLanguageActivity<ActivityMainBinding>() {

    private val mainViewModel by viewModels<MainViewModel> {
        BaseFactory {
            MainViewModel(RepositoryLocator.getMainRepo())
        }
    }

    private lateinit var navController: NavController

    override fun getViewBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    override fun initView() = with(binding) {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun getViewModel() = mainViewModel

    override fun getData() {

    }

    override fun observer() {
        viewScope {
            launch {
                mainViewModel.curLanguageSharedFlow.collect {
                    LanguageUtil.changeLanguage(
                        locale = Locale(it.languageType.languageLocalCode)
                    ) {
                        recreate() // 從新渲染activity view
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}