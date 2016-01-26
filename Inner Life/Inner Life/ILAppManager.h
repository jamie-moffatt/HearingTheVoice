//
//  ILAppManager.h
//  Inner Life
//
//  Created by Matthew Bates on 15/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ILUser.h"

@interface ILAppManager : NSObject

#define UD_FIRST_RUN_KEY  @"first_run"
#define UD_USER_KEY @"user"
#define UD_STARTING_DATE_KEY @"starting_date"
#define UD_SESSIONS_COMPLETE @"sessions_complete"
#define UD_SESSIONS_SUBMITTED @"sessions_submitted"
#define UD_AM_NOTIFICATION_TIME @"am_notification_time"
#define UD_PM_NOTIFICATION_TIME @"pm_notification_time"
#define UD_NOTIFICATION_MAP @"notification_map"
#define UD_AVERAGE_RESPONSE_TIME @"average_response_time"
#define UD_PERMISSIONS_ARE_VALID @"permissions_are_valid"
#define UD_PERMISSION_TO_STUDY_DATA @"permission_to_study_data"
#define UD_PERMISSIONS_HAVE_SYNCED @"permissions_have_synced"
#define UD_STUDY_CODE @"study_code"

+ (void)setDefaults;
+ (NSString *)userDefaultsToString;

+ (BOOL)isFirstRun;
+ (void)setFirstRun :(BOOL)isFirstRun;

+ (ILUser *)getUser;
+ (void)setUser :(ILUser *)user;
+ (BOOL)userIsRegistered;

+ (NSDate *)getStartDate;
+ (void)setStartDate :(NSDate *)date;

+ (NSDictionary *)getSessionsCompleted;
+ (void)setSessionsCompleted :(NSDictionary *)dictionary;

+ (NSDictionary *)getSessionsSubmitted;
+ (void)setSessionsSubmitted :(NSDictionary *)dictionary;

+ (BOOL)isFirstTraitSessionComplete;
+ (BOOL)isFirstTraitSessionSubmitted;

+ (BOOL)isMiddleTraitSessionComplete;
+ (BOOL)isMiddleTraitSessionSubmitted;

+ (BOOL)isLastTraitSessionComplete;
+ (BOOL)isLastTraitSessionSubmitted;

+ (NSInteger)getAMNotificationTime;
+ (void)setAMNotificationTime :(NSInteger)time;

+ (NSInteger)getPMNotificationTime;
+ (void)setPMNotificationTime :(NSInteger)time;

+ (NSDictionary *)getNotificationMap;
+ (void)setNotificationMap:(NSDictionary *)dictionary;

+ (void)updateAverageResponseTime :(NSDate *)notificationTime :(NSDate *)completionTime;
+ (NSInteger )getAverageResponseTime;
+ (NSString *)getFormattedAverageResponseTime;

+ (void)setupNotifications;
+ (void)changeNotifications :(NSInteger)newAMTime :(NSInteger)newPMTime;

+ (BOOL)dataPermissionsAreValid;
+ (void)setDataPermissionsValid :(BOOL)permissionsAreValid;

+ (BOOL)permissionToStudyData;
+ (void)setPermissionToStudyData :(BOOL)permissionToSudyData;

+ (BOOL)permissionsHaveSynchronized;
+ (void)setPermissionsHaveSynchronized :(BOOL)dataHasSynced;

+ (NSString *)getStudyCode;
+ (void)setStudyCode :(NSString *)studyCode;

@end
