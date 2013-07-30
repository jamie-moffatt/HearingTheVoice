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

@end
