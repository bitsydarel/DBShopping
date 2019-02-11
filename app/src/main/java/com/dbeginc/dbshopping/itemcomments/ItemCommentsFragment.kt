/*
 *
 *  * Copyright (C) 2017 Darel Bitsy
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License
 *
 */

package com.dbeginc.dbshopping.itemcomments


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.support.constraint.ConstraintSet
import android.support.design.widget.Snackbar
import android.support.transition.AutoTransition
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v4.app.Fragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentItemCommentsBinding
import com.dbeginc.dbshopping.itemcomments.comments.CommentsAdapter
import com.dbeginc.dbshopping.itemcomments.comments.LocationCommentViewHolder
import com.dbeginc.dbshopping.utils.extensions.getValue
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.extensions.snack
import com.dbeginc.dbshopping.utils.helper.*
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.ACCESS_LOCATION
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.ITEM_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LIST_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LOADING_PERIOD
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.RECORD_AUDIO
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.WRITE_AND_READ_EXTERNAL_STORAGE
import com.dbeginc.domain.entities.data.CommentType
import com.dbeginc.lists.itemcomment.ItemCommentsView
import com.dbeginc.lists.itemcomment.ItemCommentsViewModel
import com.dbeginc.lists.viewmodels.CommentModel
import com.dbeginc.lists.viewmodels.ItemModel
import com.dbeginc.lists.viewmodels.toDateWithTime
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.GoogleMap
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import java.io.File
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ItemCommentsFragment : BaseFragment(), ItemCommentsView, PermissionCallback, CommentActionBridge, WithParentRequiredArguments {
    private lateinit var viewModel: ItemCommentsViewModel
    private lateinit var binding: FragmentItemCommentsBinding
    private lateinit var listId: String
    private lateinit var itemId: String
    private lateinit var recorder: MediaRecorder
    private lateinit var voiceRecordPath : File
    private lateinit var lastRecordedSessionDuration: Instant
    private var isRecording = false
    private val commentsAdapter by lazy { CommentsAdapter(actionBridge = this) }
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }
    private val commentsObserver = Observer<List<CommentModel>> { commentsAdapter.updateData(it!!) }
    private val itemObserver = Observer<ItemModel> {
        binding.item = it

        binding.itemCommentToolbar.subtitle =
                getString(
                        R.string.itemBoughtBy,
                        if (it!!.bought) it.boughtBy else getString(R.string.noOneYet)
                )

        binding.executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun newInstance(args: Bundle) : ItemCommentsFragment {
            assert(args.containsKey(LIST_DATA_KEY))

            assert(args.containsKey(ITEM_DATA_KEY))

            return ItemCommentsFragment().apply {
                arguments = args
            }
        }
    }

    override fun retrieveParentArguments(): Bundle {
        return Bundle().apply {
            putString(LIST_DATA_KEY, listId)
            putString(ITEM_DATA_KEY, itemId)
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        if (savedState == null) {
            listId = arguments!!.getString(LIST_DATA_KEY)
            itemId = arguments!!.getString(ITEM_DATA_KEY)

        } else {
            listId = savedState.getString(LIST_DATA_KEY)
            itemId = savedState.getString(ITEM_DATA_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LIST_DATA_KEY, listId)
        outState.putString(ITEM_DATA_KEY, itemId)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[ItemCommentsViewModel::class.java]

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent()
                .observe(this, stateObserver)

        viewModel.getItem()
                .observe(this, itemObserver)

        viewModel.getComments()
                .observe(this, commentsObserver)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == WRITE_AND_READ_EXTERNAL_STORAGE
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            onPermissionGranted(CommentType.IMAGE)

        } else if (requestCode == RECORD_AUDIO
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted(CommentType.VOICE)

        } else if (requestCode == ACCESS_LOCATION
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted(CommentType.LOCATION)
        } else onPermissionDenied()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ConstantHolder.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageLocalPath = Matisse.obtainResult(data).first().toString()

            viewModel.presenter.validateImageComment(imageLocalPath, this)
        }
        else if (requestCode == ConstantHolder.REQUEST_LOCATION_PLACE && resultCode == Activity.RESULT_OK) {
            val place: Place = PlacePicker.getPlace(context, data)

            val latLng = place.latLng

            viewModel.presenter.validateLocationComment(
                    place.address.toString(),
                    latLng.latitude,
                    latLng.longitude,
                    this
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_item_comments,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (itemId != viewModel.presenter.itemId) {
            viewModel.presenter.itemId = itemId

            viewModel.resetState()
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.itemCommentToolbar)

        recorder = MediaRecorder()

        viewModel.presenter.bind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        recorder.release()
    }

    override fun setupView() {
        binding.itemCommentProgressBar.hideWithAnimation()

        binding.itemCommentToolbar.setNavigationOnClickListener {
            MainNavigator.goToItemDetailScreen(
                    activity as MainActivity,
                    retrieveParentArguments()
            )
        }


        binding.sendImageMessage.setOnClickListener { onImageCommentRequested() }

        binding.sendSoundMessage.setOnClickListener { onSoundCommentRequested() }

        binding.sendLocationMessage.setOnClickListener { onLocationCommentRequested() }

        binding.cancelRecording.setOnClickListener {
            if (isRecording) stopRecording() else onVoiceCommentChange(shouldShowVoiceLayout = false)
        }

        binding.sendMessage.setOnClickListener {
            viewModel.presenter.validateTextComment(
                    binding.itemMessage.getValue(),
                    this
            )
        }

        binding.voiceRecordingStatus.setOnClickListener {
            if (isRecording) {
                stopRecording()

                viewModel.presenter.validateVoiceComment(
                        voiceRecordPath,
                        Duration.between(lastRecordedSessionDuration, Instant.now()).toMillis(),
                        this
                )

            } else startRecording()
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        layoutManager.stackFromEnd = true

        binding.itemComments.layoutManager = layoutManager

        binding.itemComments.adapter = commentsAdapter

        binding.itemComments.setRecyclerListener { viewHolder ->
            if (viewHolder is LocationCommentViewHolder) {
                viewHolder.binding
                        .locationComment
                        .getMapAsync { map ->
                            map.clear()

                            map.mapType = GoogleMap.MAP_TYPE_NONE
                        }
            }
        }

        viewModel.loadItem(listId, itemId)

        viewModel.loadAllItemComments(listId, itemId)

    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> binding.root.postDelayed(binding.itemCommentProgressBar::showWithAnimation, LOADING_PERIOD)
            RequestState.COMPLETED -> binding.root.postDelayed(binding.itemCommentProgressBar::hideWithAnimation, LOADING_PERIOD)
            RequestState.ERROR -> binding.root.postDelayed(this::onViewRequestFailed, LOADING_PERIOD)
        }
    }

    override fun onPermissionGranted(kind: CommentType) = when (kind) {
        CommentType.IMAGE -> onImageCommentRequested()
        CommentType.VOICE -> onSoundCommentRequested()
        else -> onLocationCommentRequested()
    }

    override fun onPermissionDenied() {
        Snackbar.make(binding.itemCommentLayout, R.string.user_cant_use_feature_unless_accept_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry) {
                    if (hasWritePermission()) onSoundCommentRequested()
                    else onImageCommentRequested()
                }
                .show()
    }

    private fun onViewRequestFailed() {
        binding.itemCommentProgressBar.hideWithAnimation()

        if (viewModel.getLastRequest() == RequestType.ADD) {
            commentsAdapter.setCommentFailedToBeSent(
                    viewModel.presenter.releasePendingComment()
            )
        }
    }

    override fun deleteComment(comment: CommentModel, position: Int) {
        commentsAdapter.remoteItemAt(position)

        binding.itemCommentLayout.snack("Removed ${comment.commentType.name} comment")
    }

    override fun resentComment(comment: CommentModel) {
        (activity as? CommentActionHandler)?.pushPendingComment {
            viewModel.presenter.pushPendingComment(comment)
        }

        viewModel.postComment(listId, comment)
    }

    private fun postComment(comment: CommentModel) {
        (activity as? CommentActionHandler)?.pushPendingComment {
            viewModel.presenter.pushPendingComment(comment)
        }

        commentsAdapter.addComment(comment)

        viewModel.postComment(listId, comment)
    }

    /****************************** Text Related action and behavior ******************************/
    override fun sendTextComment(commentBody: String) {
        currentUser?.let {
            binding.itemMessage.setText("")

            val timestampNow = Instant.now().toEpochMilli()

            val comment = CommentModel(
                    uniqueId = UUID.randomUUID().toString(),
                    publishTime = timestampNow.toDateWithTime(),
                    timestamp = timestampNow,
                    comment = commentBody,
                    commentType = CommentType.TEXT,
                    commentArg = null,
                    itemId = itemId,
                    userId = it.uniqueId,
                    userName = it.nickname,
                    userAvatar = it.avatar
            )

            postComment(comment)
        }
    }

    /****************************** Image Related action and behavior ******************************/
    override fun sendImageComment(imagePath: String) {
        currentUser?.let {
            val timestampNow = Instant.now().toEpochMilli()

            val comment = CommentModel(
                    uniqueId = UUID.randomUUID().toString(),
                    publishTime = timestampNow.toDateWithTime(),
                    timestamp = timestampNow,
                    comment = "",
                    commentType = CommentType.IMAGE,
                    commentArg = imagePath,
                    itemId = itemId,
                    userId = it.uniqueId,
                    userName = it.nickname,
                    userAvatar = it.avatar
            )

            postComment(comment)
        }
    }

    override fun onImageExpandRequest(comment: CommentModel) {
        MainNavigator.goToExpandedImageScreen(
                activity as MainActivity,
                listId,
                itemId,
                comment.commentArg!!,
                comment.userName,
                comment.userAvatar
        )
    }

    private fun onImageCommentRequested() {
        if (hasWritePermission()) {
            Matisse.from(this)
                    .choose(MimeType.ofImage())
                    .maxSelectable(1)
                    .thumbnailScale(0.85f)
                    .showSingleMediaType(true)
                    .theme(R.style.Matisse_Zhihu)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED)
                    .imageEngine(DBShoppingEngine())
                    .capture(true)
                    .captureStrategy(CaptureStrategy(true, ConstantHolder.FILE_PROVIDER))
                    .forResult(ConstantHolder.REQUEST_IMAGE_CAPTURE)

        } else requestWriteAndReadPermission()
    }

    /****************************** Location Related action and behavior ******************************/
    override fun sendLocationComment(address: String, latitude: Double, longitude: Double) {
        currentUser?.let {
            val timestampNow = Instant.now().toEpochMilli()

            val comment = CommentModel(
                    uniqueId = UUID.randomUUID().toString(),
                    publishTime = timestampNow.toDateWithTime(),
                    timestamp = timestampNow,
                    comment = address,
                    commentType = CommentType.LOCATION,
                    commentArg = "$latitude,$longitude",
                    itemId = itemId,
                    userId = it.uniqueId,
                    userName = it.nickname,
                    userAvatar = it.avatar
            )

            postComment(comment)
        }
    }

    private fun onLocationCommentRequested() {
        if (hasLocationPermission()) showPlacePicker()
        else requestLocationPermission()
    }

    private fun showPlacePicker() {
        val placePickerIntent = PlacePicker.IntentBuilder()
                .build(activity)

        startActivityForResult(placePickerIntent, ConstantHolder.REQUEST_LOCATION_PLACE)
    }

    /************************************ Voice Related action & behavior ***********************************/
    override fun sendVoiceComment(voicePath: String, duration: Long) {
        currentUser?.let {
            val timestampNow = Instant.now().toEpochMilli()

            val comment = CommentModel(
                    uniqueId = UUID.randomUUID().toString(),
                    publishTime = timestampNow.toDateWithTime(),
                    timestamp = timestampNow,
                    comment = duration.toString(),
                    commentType = CommentType.VOICE,
                    commentArg = voicePath,
                    itemId = itemId,
                    userId = it.uniqueId,
                    userName = it.nickname,
                    userAvatar = it.avatar
            )

            postComment(comment)
        }
    }

    override fun onPlayVoiceCommentRequest(trackPath: String) =
            (activity as CommentActionHandler).playRecordedVoice(trackPath)

    override fun onStopPlayingVoiceCommentRequest() =
            (activity as CommentActionHandler).stopCurrentRecordedVoicePlaying()

    private fun onSoundCommentRequested() {
        if (hasRecordAudioPermission()) onVoiceCommentChange(shouldShowVoiceLayout = true)
        else requestRecordAudioPermission()
    }

    private fun startRecording() {
        voiceRecordPath = File(context?.cacheDir, "voice${Random().nextInt()}.3gp")

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(voiceRecordPath.path)
            prepare()
        }

        recorder.start()

        binding.recordingTime.base = SystemClock.elapsedRealtime()

        binding.recordingTime.start()

        lastRecordedSessionDuration = Instant.now()

        isRecording = true

        binding.voiceRecordingStatus.changeAnimation(getString(R.string.stop_recording))
    }

    private fun stopRecording() {
        recorder.stop()

        recorder.reset()

        binding.recordingTime.stop()

        isRecording = false

        binding.voiceRecordingStatus.changeAnimation(getString(R.string.start_recording))

        onVoiceCommentChange(shouldShowVoiceLayout = false)
    }

    private fun onVoiceCommentChange(shouldShowVoiceLayout: Boolean) {
        val layoutWithoutSound = ConstraintSet()
        val layoutWithSound = ConstraintSet()

        layoutWithoutSound.clone(binding.itemMessageContainer)
        layoutWithSound.clone(binding.itemMessageContainer)

        if (shouldShowVoiceLayout) layoutWithSound.showRecordVoiceLayout() else layoutWithSound.hideRecordVoiceLayout()

        val sets = TransitionSet()

        val autoTransition = AutoTransition()

        autoTransition.interpolator = FastOutSlowInInterpolator()

        autoTransition.duration = 1000

        val changeBounds = ChangeBounds()

        changeBounds.interpolator = FastOutSlowInInterpolator()

        changeBounds.duration = 1000

        sets.addTransition(autoTransition)

        sets.addTransition(changeBounds)

        TransitionManager.beginDelayedTransition(binding.itemMessageContainer, sets)

        layoutWithSound.applyTo(binding.itemMessageContainer)
    }

    private fun ConstraintSet.showRecordVoiceLayout() {
        // Remove messaging options
        setVisibility(binding.sendImageMessage.id, View.GONE)
        setVisibility(binding.sendSoundMessage.id, View.GONE)
        setVisibility(binding.sendLocationMessage.id, View.GONE)

        // Show recording message options
        setVisibility(binding.cancelRecording.id, View.VISIBLE)
        setVisibility(binding.recordingTimeBackground.id, View.VISIBLE)
        setVisibility(binding.recordingTime.id, View.VISIBLE)
        setVisibility(binding.voiceRecordingStatus.id, View.VISIBLE)

        connect(binding.itemMessage.id, ConstraintSet.BOTTOM, binding.recordingTimeBackground.id, ConstraintSet.TOP)
    }

    private fun ConstraintSet.hideRecordVoiceLayout() {
        // Remove messaging options
        setVisibility(binding.sendImageMessage.id, View.VISIBLE)
        setVisibility(binding.sendSoundMessage.id, View.VISIBLE)
        setVisibility(binding.sendLocationMessage.id, View.VISIBLE)

        // Show recording message options
        setVisibility(binding.cancelRecording.id, View.GONE)
        setVisibility(binding.recordingTimeBackground.id, View.GONE)
        setVisibility(binding.recordingTime.id, View.GONE)
        setVisibility(binding.voiceRecordingStatus.id, View.GONE)

        connect(binding.itemMessage.id, ConstraintSet.BOTTOM, binding.sendImageMessage.id, ConstraintSet.TOP)
    }

    private fun LottieAnimationView.changeAnimation(animationName: String) {
        hideWithAnimation()

        pauseAnimation()

        setAnimation(animationName, LottieAnimationView.CacheStrategy.Strong)

        playAnimation()

        showWithAnimation()
    }

}