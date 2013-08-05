//
//  ILAppManager.m
//  Inner Life
//
//  Created by Matthew Bates on 15/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILAppManager.h"

@implementation ILAppManager

+(void)setDefaults
{
    ILUser *defaultUser = [[ILUser alloc] init];
    defaultUser.userID = -1;
    defaultUser.age = 0;
    defaultUser.userCode = @"";
    defaultUser.gender = @"MALE";
    NSData *data = [NSKeyedArchiver archivedDataWithRootObject:defaultUser];
    
    NSDictionary *emptyDictionary = [[NSDictionary alloc] init];
    
    [[NSUserDefaults standardUserDefaults] registerDefaults:
     [NSDictionary dictionaryWithObjectsAndKeys:
      [NSNumber numberWithBool:YES], UD_FIRST_RUN_KEY,
      data, UD_USER_KEY,
      [NSDate date], UD_STARTING_DATE_KEY,
      emptyDictionary, UD_SESSIONS_COMPLETE,
      emptyDictionary, UD_SESSIONS_SUBMITTED,
      [NSNumber numberWithInteger:9], UD_AM_NOTIFICATION_TIME,
      [NSNumber numberWithInteger:15], UD_PM_NOTIFICATION_TIME,
      [NSNumber numberWithInteger:0], UD_AVERAGE_RESPONSE_TIME,
      nil]];
}

+(NSString *)userDefaultsToString
{
    NSDictionary *userDefaults = [[NSUserDefaults standardUserDefaults] dictionaryRepresentation];
    NSMutableString *s = [[NSMutableString alloc] init];
    
    for (NSString *key in userDefaults)
    {
        [s appendFormat:@"(%@, %@)\n", key, [userDefaults objectForKey:key]];
    }
    
    return s;
}

+(BOOL)isFirstRun
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:UD_FIRST_RUN_KEY];
}

+(void)setFirstRun:(BOOL)isFirstRun
{
    [[NSUserDefaults standardUserDefaults] setBool:isFirstRun forKey:UD_FIRST_RUN_KEY];
}

+(ILUser *)getUser
{
    NSData *data = [[NSUserDefaults standardUserDefaults] objectForKey:UD_USER_KEY];
    return (ILUser *)[NSKeyedUnarchiver unarchiveObjectWithData:data];
}

+(void)setUser:(ILUser *)user
{
    NSData *data = [NSKeyedArchiver archivedDataWithRootObject:user];
    [[NSUserDefaults standardUserDefaults] setObject:data forKey:UD_USER_KEY];
}

+(BOOL)userIsRegistered
{
    ILUser *user = [self getUser];
    return !(user.userID == -1);
}

+(NSDate *)getStartDate
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:UD_STARTING_DATE_KEY];
}

+(void)setStartDate:(NSDate *)date
{
    [[NSUserDefaults standardUserDefaults] setObject:date forKey:UD_STARTING_DATE_KEY];
}

+(NSDictionary *)getSessionsCompleted
{
    return [[NSUserDefaults standardUserDefaults] dictionaryForKey:UD_SESSIONS_COMPLETE];
}

+(void)setSessionsCompleted:(NSDictionary *)dictionary
{
    [[NSUserDefaults standardUserDefaults] setObject:dictionary forKey:UD_SESSIONS_COMPLETE];
}

+(NSDictionary *)getSessionsSubmitted
{
    return [[NSUserDefaults standardUserDefaults] dictionaryForKey:UD_SESSIONS_SUBMITTED];
}

+(void)setSessionsSubmitted:(NSDictionary *)dictionary
{
      [[NSUserDefaults standardUserDefaults] setObject:dictionary forKey:UD_SESSIONS_SUBMITTED];
}

+(BOOL)isFirstTraitSessionComplete
{
    return ([[self getSessionsCompleted] objectForKey:@"29"] != nil);
}
+(BOOL)isFirstTraitSessionSubmitted
{
    return ([[self getSessionsSubmitted] objectForKey:@"29"] != nil);
}

+(BOOL)isMiddleTraitSessionComplete
{
    return ([[self getSessionsCompleted] objectForKey:@"30"] != nil);
}
+(BOOL)isMiddleTraitSessionSubmitted
{
    return ([[self getSessionsSubmitted] objectForKey:@"30"] != nil);
}

+(BOOL)isLastTraitSessionComplete
{
    return ([[self getSessionsCompleted] objectForKey:@"31"] != nil);
}
+(BOOL)isLastTraitSessionSubmitted
{
    return ([[self getSessionsSubmitted] objectForKey:@"31"] != nil);
}

+(NSInteger)getAMNotificationTime
{
    return [[NSUserDefaults standardUserDefaults] integerForKey:UD_AM_NOTIFICATION_TIME];
}

+(void)setAMNotificationTime:(NSInteger)time
{
    [[NSUserDefaults standardUserDefaults] setInteger:time forKey:UD_AM_NOTIFICATION_TIME];
}

+(NSInteger)getPMNotificationTime
{
    return [[NSUserDefaults standardUserDefaults] integerForKey:UD_PM_NOTIFICATION_TIME];
}

+(void)setPMNotificationTime:(NSInteger)time
{
    [[NSUserDefaults standardUserDefaults] setInteger:time forKey:UD_PM_NOTIFICATION_TIME];
}

+ (void)updateAverageResponseTime :(NSDate *)notificationTime :(NSDate *)completionTime
{
    NSTimeInterval newResponseTime = [completionTime timeIntervalSinceDate:notificationTime];
    
    int numberOfCompletions = 0;
    NSDictionary *completionDictionary = [ILAppManager getSessionsCompleted];
    
    for (NSString *key in completionDictionary)
    {
        if ([completionDictionary objectForKey:key]) numberOfCompletions++;
    }
    
    int weightedAvgResponseTime = [ILAppManager getAverageResponseTime] * (numberOfCompletions - 1);
    int newAvgResponseTime = (weightedAvgResponseTime + (int)newResponseTime) / numberOfCompletions;
    
    [[NSUserDefaults standardUserDefaults] setInteger:newAvgResponseTime forKey:UD_AVERAGE_RESPONSE_TIME];
}

+ (NSInteger)getAverageResponseTime
{
    return [[NSUserDefaults standardUserDefaults] integerForKey:UD_AVERAGE_RESPONSE_TIME];
}

+ (NSString *)getFormattedAverageResponseTime
{
    int averageResponseTime = [ILAppManager getAverageResponseTime];
    
    int seconds = averageResponseTime % 60;
    int minutes = (averageResponseTime % (60 * 60)) / 60;
    int hours = averageResponseTime / (60 * 60);
    
    NSMutableString *s = [[NSMutableString alloc] init];
    
    if (averageResponseTime > (60 * 60))
    {
        [s appendFormat:@"%d Hour%@ ", hours, hours > 1 ? @"s" : @""];
    }
    if (averageResponseTime > 60)
    {
        [s appendFormat:@"%d Minute%@ ", minutes, minutes > 1 ? @"s" : @""];
    }
    [s appendFormat:@"%d Second%@ ", seconds, seconds > 1 ? @"s" : @""];
    
    return [s copy];
}

+(void)setupNotifications
{
    [[UIApplication sharedApplication] cancelAllLocalNotifications];
    
    NSDate *startDate = [ILAppManager getStartDate];
    
    NSDateComponents* dc = [[NSCalendar currentCalendar] components:(NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit) fromDate:startDate];
    dc.day = dc.day+1;
    NSDate* dayAfterRegistration = [[NSCalendar currentCalendar] dateFromComponents:dc];
    
    NSInteger amTime = [ILAppManager getAMNotificationTime];
    NSInteger pmTime = [ILAppManager getPMNotificationTime];
    
    for (int i = 0; i < 14; i++)
    {
        NSDateComponents* dc = [[NSCalendar currentCalendar] components:(NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit) fromDate:dayAfterRegistration];
        dc.day = dc.day + i;
        
        dc.hour = amTime;
        NSDate *amNotificationTime = [[NSCalendar currentCalendar] dateFromComponents:dc];
        
        dc.hour = pmTime;
        NSDate *pmNotificationTime = [[NSCalendar currentCalendar] dateFromComponents:dc];
        
        UILocalNotification *amNotification = [[UILocalNotification alloc] init];
        amNotification.fireDate = amNotificationTime;
        amNotification.timeZone = [NSTimeZone defaultTimeZone];
        amNotification.alertBody = [NSString stringWithFormat:@"Questions Available. (AM)"];
        amNotification.alertAction = @"View Details";
        amNotification.soundName = UILocalNotificationDefaultSoundName;
        amNotification.applicationIconBadgeNumber = 2*i + 1;
        [[UIApplication sharedApplication] scheduleLocalNotification:amNotification];
        
        UILocalNotification *pmNotification = [[UILocalNotification alloc] init];
        pmNotification.fireDate = pmNotificationTime;
        pmNotification.timeZone = [NSTimeZone defaultTimeZone];
        pmNotification.alertBody = [NSString stringWithFormat:@"Questions Available. (PM)"];
        pmNotification.alertAction = @"View Details";
        pmNotification.soundName = UILocalNotificationDefaultSoundName;
        pmNotification.applicationIconBadgeNumber = 2*i + 2;
        [[UIApplication sharedApplication] scheduleLocalNotification:pmNotification];
    }
}

@end
