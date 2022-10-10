package com.baontq.mob201.ui.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.baontq.mob201.R;
import com.baontq.mob201.databinding.FragmentMusicBinding;
import com.baontq.mob201.service.PlayerService;
import com.baontq.mob201.until.MusicUntil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MusicFragment extends Fragment {
    public static final String TAG = "MusicFragment";
    private FragmentMusicBinding binding;
    private PlayerService playerService;
    private boolean isBound = false;
    private StateReceiver receiver = new StateReceiver();
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            playerService = ((PlayerService.ServiceBinder) binder).getInstance();
            updateUI();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerService = null;
            isBound = false;
        }
    };
    private ScheduledFuture<?> scheduledFuture;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MusicViewModel musicViewModel =
                new ViewModelProvider(this).get(MusicViewModel.class);

        binding = FragmentMusicBinding.inflate(inflater, container, false);
        binding.sbSongProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binding.tvPlayerCurrentTime.setText(MusicUntil.milliSecondsToTimer(seekBar.getProgress() * 1000));
                playerService.seek(seekBar.getProgress() * 1000);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.ibPlayerActionPlayPause.setOnClickListener(v -> {
            if (playerService != null) {
                if (playerService.isPlaying()) {
                    playerService.pause();
                    scheduledFuture.cancel(false);
                    binding.ibPlayerActionPlayPause.setImageResource(R.drawable.ic_play_white);
                } else {
                    playerService.resume();
                    if (scheduledFuture == null || scheduledFuture.isCancelled()) {
                        updateUI();
                    }
                    binding.ibPlayerActionPlayPause.setImageResource(R.drawable.ic_pause_white);
                }
            }
        });
        binding.ibPlayerActionNext.setOnClickListener(v -> {
            playerService.next();
        });

        binding.ibPlayerActionPrev.setOnClickListener(v -> {
            playerService.prev();
        });
    }

    private void updateUI() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long totalDuration = playerService.getMediaPlayer().getDuration();
                long currentDuration = playerService.getMediaPlayer()
                        .getCurrentPosition();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvPlayerSongTitle.setText(playerService.getSongPlaying().getTitle());
                        binding.tvPlayerSongDescription.setText(playerService.getSongPlaying().getArtistName());
                        // Displaying Total Duration time
                        binding.tvPlayerSongDuration.setText(MusicUntil.milliSecondsToTimer(totalDuration));
                        // Displaying time completed playing
                        binding.tvPlayerCurrentTime.setText(MusicUntil.milliSecondsToTimer(currentDuration));
                        // Updating progress bar
                        int progress = playerService.getMediaPlayer().getCurrentPosition() / 1000;
                        binding.sbSongProgress.setProgress(progress);
                        if (playerService.isPlaying()) {
                            binding.ibPlayerActionPlayPause.setImageResource(R.drawable.ic_pause_white);
                        } else {
                            binding.ibPlayerActionPlayPause.setImageResource(R.drawable.ic_play_white);
                            scheduledFuture.cancel(false);
                        }
                    }
                });

                Log.d(TAG, "run: " + playerService.getMediaPlayer().getCurrentPosition());
            }
        };
        if (playerService != null && playerService.isPlaying()) {
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
            binding.ibPlayerActionPlayPause.setImageResource(R.drawable.ic_pause_white);
            binding.sbSongProgress.setMax((int) (playerService.getSongPlaying().getDuration() / 1000));
        }
    }

    class StateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(PlayerService.ACTION_PREV) || intent.getAction().equalsIgnoreCase(PlayerService.ACTION_NEXT)) {
                if (scheduledFuture == null || scheduledFuture.isCancelled()) {
                    updateUI();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_NEXT);
        intentFilter.addAction(PlayerService.ACTION_PREV);
        requireActivity().registerReceiver(receiver, intentFilter);
        requireActivity().bindService(new Intent(getActivity(), PlayerService.class), connection, Context.BIND_AUTO_CREATE);
        isBound = true;
        Log.d(TAG, "onStart: Bind Service");
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().unregisterReceiver(receiver);
        if (isBound) {
            if (scheduledFuture != null)
                scheduledFuture.cancel(false);
            requireActivity().unbindService(connection);
            isBound = false;
            Log.d(TAG, "onStop: Unbind Service");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}