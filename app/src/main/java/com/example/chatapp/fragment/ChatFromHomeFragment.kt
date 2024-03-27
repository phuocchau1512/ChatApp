package com.example.chatapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.adapter.MessageAdapter
import com.example.chatapp.adapter.RecentChatAdapter
import com.example.chatapp.databinding.FragmentChatfromHomeBinding
import com.example.chatapp.modal.Messages
import com.example.chatapp.mvvm.ChatAppViewModel


class ChatFromHomeFragment : Fragment() {

    private lateinit var args: ChatFromHomeFragmentArgs
    private lateinit var binding: FragmentChatfromHomeBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chatfrom_home,container,false)
        args = ChatFromHomeFragmentArgs.fromBundle(requireArguments())

        viewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        binding.viewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // load other users
        Glide.with(requireContext()).load(args.recentChats.friendImage).into(binding.chatImageViewUser)
        binding.chatUserStatus.text = args.recentChats.status
        binding.chatUserName.text = args.recentChats.name

        binding.chatBackBtn.setOnClickListener{
            val action = ChatFromHomeFragmentDirections.actionChatFromHomeFragmentToHomeFragment()
            val navController = view.findNavController()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
            navController.navigate(action, navOptions)
        }


        viewModel.message.observe(viewLifecycleOwner){
            if ( it != "" ) binding.sendBtn.setImageResource(R.drawable.baseline_send_24)
            else binding.sendBtn.setImageResource(R.drawable.baseline_send_25)
        }

        binding.sendBtn.setOnClickListener{
            if ( viewModel.message.value != null ) viewModel.sendMessage(args.recentChats)
        }

        viewModel.getMessages(args.recentChats).observe(viewLifecycleOwner){
            initRecyclerView(it)
        }
    }

    private fun initRecyclerView(it: List<Messages>?) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        binding.messagesRecyclerView.layoutManager=layoutManager
        if (it != null) {
            messageAdapter.setMessageList(it)
        }
        binding.messagesRecyclerView.adapter = messageAdapter
    }

}