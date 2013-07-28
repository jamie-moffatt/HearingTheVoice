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

- (NSArray *)getQuestionsInSectionsFilteredBySession: (NSInteger)sessionID
{
    NSMutableArray *filteredSectionList = [[NSMutableArray alloc] init];
    NSArray *session = [[self getSchedule].sessions objectAtIndex:sessionID-1];
    
    for (int i = 0; i < [session count]; i++)
    {
        NSInteger sectionID = [[session objectAtIndex:i] integerValue];
        [filteredSectionList addObject:[[self getQuestionsInSections] objectAtIndex:(sectionID - 1)]];
    }
    
    return [filteredSectionList copy];
}

- (NSArray *)getQuestionsBySection: (NSInteger)sectionID
{
    NSArray *sectionList = [self getQuestionsInSections];
    ILSection *section = [sectionList objectAtIndex:sectionID-1];
    return section.questions;
}

- (NSArray *)getFlatQuestionArrayBySession: (NSInteger)sessionID
{
    NSArray *session = [[self getSchedule].sessions objectAtIndex:(sessionID - 1)];
    NSLog(@"Session %d:\n\n", sessionID);
    NSLog(@"%@", session);
    NSMutableArray *questions = [[NSMutableArray alloc] init];
    for (int i = 0; i < [session count]; i++)
    {
        NSInteger sectionID = [[session objectAtIndex:i] integerValue];
        [questions addObjectsFromArray:[self getQuestionsBySection:sectionID]];
    }
    return [questions copy];
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
