package host.exp.exponent.notifications.channels;

import android.app.NotificationChannelGroup;
import android.content.Context;
import android.os.Build;

import org.unimodules.core.arguments.ReadableArguments;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import expo.modules.notifications.notifications.channels.managers.AndroidXNotificationsChannelGroupManager;
import host.exp.exponent.kernel.ExperienceId;

public class ScopedNotificationsGroupManager extends AndroidXNotificationsChannelGroupManager {
  private ExperienceId mExperienceId;

  public ScopedNotificationsGroupManager(Context context, ExperienceId experienceId) {
    super(context);
    mExperienceId = experienceId;
  }

  @Nullable
  @Override
  @RequiresApi(api = Build.VERSION_CODES.O)
  public NotificationChannelGroup getNotificationChannelGroup(@NonNull String channelGroupId) {
    NotificationChannelGroup scopedGroup = super.getNotificationChannelGroup(ScopedNotificationsChannelUtils.getScopedGroupId(mExperienceId, channelGroupId));
    if (scopedGroup != null) {
      return scopedGroup;
    }

    // In SDK 38 groups weren't scoped, so we want to return unscoped channel if the scoped one wasn't found.
    return super.getNotificationChannelGroup(channelGroupId);
  }

  @NonNull
  @Override
  @RequiresApi(api = Build.VERSION_CODES.O)
  public List<NotificationChannelGroup> getNotificationChannelGroups() {
    ArrayList<NotificationChannelGroup> result = new ArrayList<>();
    List<NotificationChannelGroup> channelGroups = super.getNotificationChannelGroups();
    for (NotificationChannelGroup group : channelGroups) {
      if (ScopedNotificationsChannelUtils.checkIfGroupBelongsToExperience(mExperienceId, group)) {
        result.add(group);
      }
    }

    return result;
  }

  @Override
  @RequiresApi(api = Build.VERSION_CODES.O)
  public NotificationChannelGroup createNotificationChannelGroup(@NonNull String groupId, @NonNull CharSequence name, ReadableArguments groupOptions) {
    return super.createNotificationChannelGroup(ScopedNotificationsChannelUtils.getScopedGroupId(mExperienceId, groupId), name, groupOptions);
  }

  @Override
  @RequiresApi(api = Build.VERSION_CODES.O)
  public void deleteNotificationChannelGroup(@NonNull String groupId) {
    NotificationChannelGroup groupToRemove = getNotificationChannelGroup(groupId);
    if (groupToRemove != null) {
      super.deleteNotificationChannelGroup(groupToRemove.getId());
    }
  }
}
