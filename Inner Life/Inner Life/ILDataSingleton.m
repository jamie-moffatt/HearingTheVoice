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
        if ([ILDataSingleton questionsAreCached])
        {
            sections = [ILDataSingleton questionsFromCache];
        }
        else
        {
            sections = [ILNetworkDataHandler downloadAndParseQuestionsInSections];
        }
    }
    return sections;
}

+ (BOOL)questionsAreCached
{
    NSFileManager* fm = [NSFileManager defaultManager];
    NSString* appSupportDirStr = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) objectAtIndex:0];;
    return [fm fileExistsAtPath:[appSupportDirStr stringByAppendingPathComponent:@"questions.xml"]];
}

+ (NSArray *)questionsFromCache
{
    NSFileManager* fm = [[NSFileManager alloc] init];
    NSURL* appSupportDirURL = [fm URLForDirectory:NSApplicationSupportDirectory inDomain:NSUserDomainMask appropriateForURL:nil create:YES error:nil];
    NSData* cachedData = [[NSData alloc] initWithContentsOfURL:[appSupportDirURL URLByAppendingPathComponent:@"questions.xml"]];
    
    NSMutableArray *sectionList = [[NSMutableArray alloc] init];
    
    ILQuestionXMLParserDelegate *xmlParserDelegate = [[ILQuestionXMLParserDelegate alloc] init];
    xmlParserDelegate.sectionList = sectionList;
    
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:cachedData];
    [xmlParser setDelegate:xmlParserDelegate];
    
    if ([xmlParser parse])
    {
        NSLog(@"Questions parsed successfully from cache.");
    }
    else
    {
        NSLog(@"An ERROR occurred whilst parsing Question XML from cache.");
    }
    
    return [sectionList copy];

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
    NSLog(@"Session %ld:\n\n",(long)sessionID);
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
        if ([ILDataSingleton scheduleIsCached])
        {
            schedule = [ILDataSingleton scheduleFromCache];
        }
        else
        {
            schedule = [ILNetworkDataHandler downloadAndParseSchedule];
        }
    }
    return schedule;
}

+ (BOOL)scheduleIsCached
{
    NSFileManager* fm = [NSFileManager defaultManager];
    NSString* appSupportDirStr = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) objectAtIndex:0];;
    return [fm fileExistsAtPath:[appSupportDirStr stringByAppendingPathComponent:@"schedule.xml"]];

}

+ (ILSchedule *)scheduleFromCache
{
    ILSchedule *schedule = [[ILSchedule alloc] init];
    
    ILScheduleXMLParserDelegate *xmlParserDelegate = [[ILScheduleXMLParserDelegate alloc] init];
    xmlParserDelegate.schedule = schedule;
    
    NSFileManager* fm = [[NSFileManager alloc] init];
    NSURL* appSupportDirURL = [fm URLForDirectory:NSApplicationSupportDirectory inDomain:NSUserDomainMask appropriateForURL:nil create:YES error:nil];
    NSData* cachedData = [[NSData alloc] initWithContentsOfURL:[appSupportDirURL URLByAppendingPathComponent:@"schedule.xml"]];
    
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:cachedData];
    [xmlParser setDelegate:xmlParserDelegate];
    
    if ([xmlParser parse])
    {
        NSLog(@"Schedule parsed successfully from cache.");
    }
    else
    {
        NSLog(@"An ERROR occurred whilst parsing Schedule XML from cache.");
    }

    return schedule;
}

@end
