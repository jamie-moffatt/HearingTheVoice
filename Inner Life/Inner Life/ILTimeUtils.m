//
//  ILTimeUtils.m
//  Inner Life
//
//  Created by Matthew Bates on 15/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILTimeUtils.h"

@implementation ILTimeUtils

+ (NSInteger)getSessionByRegistrationDate:(NSDate*)date
{
    if (ABS([date timeIntervalSinceNow]) < 60*60*24) return -1;
    
    NSDateComponents* dateComps = [[NSCalendar currentCalendar] components:(NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit) fromDate:date];
    dateComps.day = dateComps.day+1;
    NSDate* dayAfterRegistration = [[NSCalendar currentCalendar] dateFromComponents:dateComps];
    
    NSTimeInterval timeOnSchedule = [[NSDate date] timeIntervalSinceDate:dayAfterRegistration];
    return ((NSInteger)timeOnSchedule / (60*60*12)) + 1;
}
@end