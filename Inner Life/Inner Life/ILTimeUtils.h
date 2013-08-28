//
//  ILTimeUtils.h
//  Inner Life
//
//  Created by Matthew Bates on 15/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ILTimeUtils : NSObject

+ (NSInteger)getSessionByRegistrationDate:(NSDate*)date;
+ (NSDate *)dayAfter :(NSDate *)date;
+ (NSInteger)secondsOnCurrentDay;

@end
