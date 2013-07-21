//
//  ILDataSingleton.m
//  Inner Life
//
//  Created by Matthew Bates on 15/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILDataSingleton.h"
#import "ILNetworkDataHandler.h"

@implementation ILDataSingleton

+ (ILDataSingleton *)instance
{
    static ILDataSingleton *sInstance;
    @synchronized(self)
    {
        if (!sInstance)
        {
            sInstance = [[ILDataSingleton alloc] init];
        }
        return sInstance;
    }
}

// Return an array of sections
- (NSArray *)getQuestionsInSections
{
    if (sections)
    {
        return sections;
    }
    else
    {
        return [ILNetworkDataHandler downloadAndParseQuestionsInSections];
    }
}

// Return a schedule object either from the cache or by accessing the web API
- (ILSchedule *)getSchedule
{
    if (schedule)
    {
        return schedule;
    }
    else
    {
        return [ILNetworkDataHandler downloadAndParseSchedule];
    }
}

@end
