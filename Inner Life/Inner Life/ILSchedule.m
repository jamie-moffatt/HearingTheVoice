//
//  ILSchedule.m
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILSchedule.h"

@implementation ILSchedule

- (NSString*)description
{
    NSMutableString *sb = [NSMutableString new];
    for (NSMutableArray *session in _sessions)
    {
        [sb appendFormat:@"%@\n", session];
    }
    return sb;
}

@end
