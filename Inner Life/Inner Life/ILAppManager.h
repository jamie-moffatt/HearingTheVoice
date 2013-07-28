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

@end
