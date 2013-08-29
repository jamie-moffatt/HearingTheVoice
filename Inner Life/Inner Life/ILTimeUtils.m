//
//  ILTimeUtils.m
//  Inner Life
//
//  Created by Matthew Bates on 15/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILTimeUtils.h"

@implementation ILTimeUtils

+ (NSInteger)getSessionByRegistrationDate: (NSDate *)date
{
    NSDateComponents* dateComps = [[NSCalendar currentCalendar] components:(NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit) fromDate:date];
    dateComps.day = dateComps.day+1;
    NSDate* dayAfterRegistration = [[NSCalendar currentCalendar] dateFromComponents:dateComps];
    
    NSDate* now = [NSDate date];
    
    if ([now compare:dayAfterRegistration] == NSOrderedAscending) return -1;
    
    NSTimeInterval timeOnSchedule = [[NSDate date] timeIntervalSinceDate:dayAfterRegistration];
    return ((NSInteger)timeOnSchedule / (60*60*12)) + 1;
}

+ (NSDate *)dayAfter :(NSDate *)date
{
    NSDateComponents* dc = [[NSCalendar currentCalendar] components:(NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit) fromDate:date];
    dc.day = dc.day+1;
    return [[NSCalendar currentCalendar] dateFromComponents:dc];
}

+ (NSInteger)secondsOnCurrentDay
{
    NSDateComponents* nsdc = [[NSCalendar currentCalendar] components:(NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit) fromDate:[NSDate date]];
    return ABS([[[NSCalendar currentCalendar] dateFromComponents:nsdc] timeIntervalSinceNow]);
}

@end