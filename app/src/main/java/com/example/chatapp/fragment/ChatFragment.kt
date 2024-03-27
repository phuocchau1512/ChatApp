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
import com.example.chatapp.databinding.FragmentChatBinding
import com.example.chatapp.modal.Messages
import com.example.chatapp.mvvm.ChatAppViewModel


class ChatFragment : Fragment() {

    private lateinit var args: ChatFragmentArgs
    private lateinit var binding: FragmentChatBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat,container,false)
        args = ChatFragmentArgs.fromBundle(requireArguments())

        viewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        binding.viewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chatBackBtn.setOnClickListener{
            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
            view.findNavController().popBackStack()
        }

        // load other users
        Glide.with(requireContext()).load(args.users.imageUrl).into(binding.chatImageViewUser)
        binding.chatUserStatus.text = args.users.status
        binding.chatUserName.text = args.users.username

        binding.chatBackBtn.setOnClickListener{
            val action = ChatFragmentDirections.actionChatFragmentToHomeFragment()
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
            if ( viewModel.message.value != null ) viewModel.sendMessage(args.users)
        }

        viewModel.getMessages(args.users).observe(viewLifecycleOwner){
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