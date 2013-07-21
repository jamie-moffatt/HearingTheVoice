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
    if (!sections)
    {
        sections = [ILNetworkDataHandler downloadAndParseQuestionsInSections];
    }
    return sections;
}

- (NSArray *)getQuestionsBySection: (NSInteger)sectionID
{
    NSArray *sectionList = [self getQuestionsInSections];
    ILSection *section = [sectionList objectAtIndex:sectionID];
    return section.questions;
}

// Return a schedule object either from the cache or by accessing the web API
- (ILSchedule *)getSchedule
{
    if (!schedule)
    {
        schedule = [ILNetworkDataHandler downloadAndParseSchedule];
    }
    return schedule;
}

@end
