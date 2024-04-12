package com.example.chatapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.SignInActivity
import com.example.chatapp.adapter.OnUserClickListener
import com.example.chatapp.adapter.RecentChatAdapter
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.databinding.FragmentHomeBinding
import com.example.chatapp.modal.RecentChats
import com.example.chatapp.modal.Users
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment(), OnUserClickListener {

    private lateinit var userAdapter: UserAdapter
    private lateinit var userViewModel: ChatAppViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var recentChatAdapter: RecentChatAdapter




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate( inflater,R.layout.fragment_home, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]


        // hien thi cac user trong list
        userAdapter = UserAdapter()
        binding.rvUsers.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)

        userViewModel.getUsers().observe( viewLifecycleOwner) {
            userAdapter.setUserList(it)
            binding.rvUsers.adapter = userAdapter
        }

        userAdapter.setOnUserClickListener(this)

        // cai dat de log out
        binding.logOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity,SignInActivity::class.java))
            activity?.finish()
        }

        userViewModel.imageUrl.observe(viewLifecycleOwner) {
            Glide.with(requireContext()).load(it).into(binding.tlImage)
        }

        // hien thi cac recent chat
        recentChatAdapter = RecentChatAdapter()
        recentChatAdapter.setOnRecentChatListener(this)
        binding.rvRecentChats.layoutManager = LinearLayoutManager(context)
        userViewModel.getRecentChat().observe(viewLifecycleOwner){
            recentChatAdapter.setRecentChatList(it)
            binding.rvRecentChats.adapter = recentChatAdapter
        }

        binding.tlImage.setOnClickListener{
            view.findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }
    }

    override fun onUserSelected(position: Int, users: Users) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)

        Log.e("HomeFragment","Clicked On ${users.username}")
    }

    override fun getOnRecentChatClick(position: Int, recentChats: MutableList<RecentChats>) {
        val recentChat = recentChats[position]
        val action = HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(recentChat)
        view?.findNavController()?.navigate(action)
    }


}