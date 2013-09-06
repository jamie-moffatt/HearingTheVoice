//
//  Inner_LifeTests.m
//  Inner LifeTests
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "Inner_LifeTests.h"
#import "ILAppManager.h"
#import "NSDate-Utilities.h"

@implementation Inner_LifeTests

- (void)setUp
{
    [super setUp];
    // Set-up code here.
}

- (void)tearDown
{
    // Tear-down code here.
    [super tearDown];
}

- (void)testNotifications
{
    NSDate* now = [NSDate date];
    [ILAppManager setStartDate:now];
    [ILAppManager setupNotifications];
    int am = [ILAppManager getAMNotificationTime];
    int pm = [ILAppManager getPMNotificationTime];
    
    NSArray* notifications = [[UIApplication sharedApplication] scheduledLocalNotifications];
    
    int n = [notifications count];
    STAssertTrue(n == 28, @"There should be 28 notifications but there are %d", n);
    
    for (UILocalNotification* notification in notifications)
    {
        NSDate* notificationTime = notification.fireDate;
        STAssertTrue([notificationTime isLaterThanDate:now], @"@% < @%", now, notificationTime);
        
        int sessionID = [[notification.userInfo objectForKey:@"SESSION_ID"] integerValue];
        
        NSDateComponents* dc = [[NSCalendar currentCalendar] components:(NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit) fromDate:notificationTime];
        NSDateComponents* startDC = [dc copy];
        startDC.hour = (sessionID % 2) ? am : pm;
        NSDateComponents* endDC = [dc copy];
        endDC.hour = ((sessionID % 2) ? am : pm) + 3;
        
        NSDate* start = [[NSCalendar currentCalendar] dateFromComponents:startDC];
        NSDate* end = [[NSCalendar currentCalendar] dateFromComponents:endDC];
        
        STAssertTrue(
                     ([start isEarlierThanDate:notificationTime]
                     && [end isLaterThanDate:notificationTime])
                     || [start isEqualToDate:notificationTime]
                     || [end isEqualToDate:notificationTime],
                     @"%@ <= %@ <= %@", start, notificationTime, end);        
    }
}

@end
