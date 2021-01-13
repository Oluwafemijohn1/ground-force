package com.trapezoidlimited.groundforce.ui.dashboard.notifications

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.date.dayOfMonth
import com.trapezoidlimited.groundforce.EntryApplication
import com.trapezoidlimited.groundforce.R
import com.trapezoidlimited.groundforce.adapters.NotificationsAdapter
import com.trapezoidlimited.groundforce.api.ApiService
import com.trapezoidlimited.groundforce.api.MissionsApi
import com.trapezoidlimited.groundforce.api.Resource
import com.trapezoidlimited.groundforce.databinding.FragmentNotificationsBinding
import com.trapezoidlimited.groundforce.model.NotificationItem
import com.trapezoidlimited.groundforce.model.NotificationsHeader
import com.trapezoidlimited.groundforce.model.NotificationsItem
import com.trapezoidlimited.groundforce.repository.AuthRepositoryImpl
import com.trapezoidlimited.groundforce.utils.DividerItemDecoration
import com.trapezoidlimited.groundforce.utils.checkItem
import com.trapezoidlimited.groundforce.utils.handleApiError
import com.trapezoidlimited.groundforce.utils.splitDate
import com.trapezoidlimited.groundforce.viewmodel.AuthViewModel
import com.trapezoidlimited.groundforce.viewmodel.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_dashboard.*
import retrofit2.Retrofit
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


/**
 * Notificationc Fragment
 */

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    @Inject
    lateinit var loginApiServiceService: ApiService

    @Inject
    lateinit var missionsApi: MissionsApi

    @Inject
    lateinit var retrofit: Retrofit

    private lateinit var notificationsList: MutableList<NotificationItem>

    private lateinit var viewModel: AuthViewModel

    private lateinit var binding: FragmentNotificationsBinding

    private val adapter = NotificationsAdapter(mutableListOf())

    private val roomViewModel by lazy { EntryApplication.roomViewModel(this) }

    private var notification = ""
    private var date = ""
    private var dateFormatted = ""
    private var dateInString = ""
    private var timeInString = ""
    private lateinit var timeToHourMinute: List<String>
    private var hour = 0
    private var minute = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        val repository = AuthRepositoryImpl(loginApiServiceService, missionsApi)
        val factory = ViewModelFactory(repository, requireContext())
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.notificationsFragmentRecyclerNewNotificationsRv

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        val heightInPixels = resources.getDimensionPixelSize(R.dimen.list_item_divider_height)
        context?.let {
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    ContextCompat.getColor(it, R.color.colorDividerDialogVerification),
                    heightInPixels
                )
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var header = "New Notifications"
        var notificationDate = ""

        viewModel.getAllNotifications(1)

        notificationsList = mutableListOf()
        timeToHourMinute = listOf()

        viewModel.getAllNotificationsResponse.observe(viewLifecycleOwner, {

            when (it) {
                is Resource.Success -> {
                    val notificationResponseResultList = it.value.data?.data

                    if (notificationResponseResultList != null) {
                        notificationsList.clear()
                        for (notificationResponseResult in notificationResponseResultList) {

                            notificationDate = ""

                            notification = notificationResponseResult.notifications
                            date = notificationResponseResult.date
                            dateFormatted = splitDate(date)
                            dateInString = date.substring(0, 10)
                            timeInString = date.substring(11, 16)
                            timeToHourMinute = timeInString.split(":")
                            hour = timeToHourMinute[0].toInt()
                            minute = timeToHourMinute[1].toInt()

                            notificationDate = getNotificationDate(hour, minute, dateFormatted)

                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val formattedDate = sdf.format(Calendar.getInstance().time)

                            if (dateInString != formattedDate) {
                                header = "Older Notifications"
                            }


                            val notificationItem = NotificationItem(
                                image = R.drawable.bolt,
                                message = notification,
                                date = notificationDate,
                                isNew = header
                            )

                            notificationsList.add(notificationItem)
                        }

                    }

                    val notificationsItemList = mutableListOf<NotificationsItem>()
                    val notificationHeaders =
                        mutableListOf("New Notifications", "Older Notifications")

                    notificationHeaders.let {
                        for (notificationHeader in notificationHeaders) {
                            notificationsItemList.add(
                                NotificationsItem.withHeader(
                                    NotificationsHeader(notificationHeader)
                                )
                            )
                            val filtered =
                                notificationsList.filter { it.isNew == notificationHeader }
                                    .map { NotificationsItem.withMessage(it) }

                            notificationsItemList.addAll(filtered)
                        }
                    }

                    adapter.updateNotifications(notificationsItemList)
                    adapter.notifyDataSetChanged()
                }

                is Resource.Failure -> {

                    handleApiError(roomViewModel, requireActivity(), it, retrofit, requireView())

                }
            }
        })


        // Navigate to Home on Back Press
        requireActivity().onBackPressedDispatcher.addCallback {
            if (findNavController().currentDestination?.id == R.id.notificationsFragment) {
                activity?.dashboardActivity_bnv?.checkItem(R.id.agentDashboard_home)
                val action =
                    NotificationsFragmentDirections.actionNotificationsFragmentToAgentDashboardFragment()
                findNavController().navigate(action)
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun getNotificationDate(hour: Int, minute: Int, dateFormatted: String): String {

        val notificationDateBuilder = StringBuilder()

        var timeLabel = "PM"

        if (hour in 0..11 && minute in 0..59) {
            timeLabel = "AM"
        }
        notificationDateBuilder.append(dateFormatted).append("  ")
            .append(hour).append(" : ").append(minute).append(" ")
            .append(timeLabel)

        return notificationDateBuilder.toString()
    }


}