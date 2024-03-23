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
        userAdapter = UserAdapter()

        binding.rvUsers.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)


        userViewModel.getUsers().observe( viewLifecycleOwner) {
            userAdapter.setUserList(it)
            binding.rvUsers.adapter = userAdapter
        }

        userAdapter.setOnUserClickListener(this)

        binding.logOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity,SignInActivity::class.java))
            activity?.finish()
        }

        userViewModel.imageUrl.observe(viewLifecycleOwner) {
            Glide.with(requireContext()).load(it).into(binding.tlImage)
        }


    }

    override fun onUserSelected(position: Int, users: Users) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)

        Log.e("HomeFragment","Clicked On ${users.username}")
    }

    override fun getOnRecentChatClick(position: Int, recentChats: MutableList<RecentChats>) {
        TODO("Not yet implemented")
    }


}