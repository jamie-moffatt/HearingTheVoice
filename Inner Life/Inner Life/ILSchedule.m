//
//  ILSchedule.m
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILSchedule.h"

@implementation ILSchedule

- (id)init
{
    self = [super init];
    if (self)
    {
        _sessions = [[NSMutableArray alloc] init];
    }
    
    return self;
}

- (NSString*)description
{
    NSMutableString *sb = [[NSMutableString alloc] init];
    for (NSMutableArray *session in _sessions)
    {
        [sb appendFormat:@"%@\n", session];
    }
    return sb;
}

- (void)add:(NSArray *)session
{
    [_sessions addObject:session];
}

@end
