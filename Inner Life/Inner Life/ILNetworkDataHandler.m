//
//  ILNetworkDataHandler.m
//  Inner Life
//
//  Created by Matthew Bates on 15/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILNetworkDataHandler.h"
#import "ILQuestionXMLParserDelegate.h"
#import "ILScheduleXMLParserDelegate.h"
#import "common.h"

@implementation ILNetworkDataHandler

// TODO: Cache the xml and reload

+ (NSArray *)downloadAndParseQuestionsInSections
{
    NSURL *url = [NSURL URLWithString:QUESTION_API_ENDPOINT];
    
    NSMutableArray *sectionList = [[NSMutableArray alloc] init];
    
    ILQuestionXMLParserDelegate *xmlParserDelegate = [[ILQuestionXMLParserDelegate alloc] init];
    xmlParserDelegate.sectionList = sectionList;
    
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithContentsOfURL:url];
    [xmlParser setDelegate:xmlParserDelegate];
    
    if ([xmlParser parse])
    {
        NSLog(@"Questions parsed successfully.");
    }
    else
    {
        NSLog(@"An ERROR occurred whilst parsing Question XML.");
    }
    
    return [sectionList copy];
}

+ (ILSchedule *)downloadAndParseSchedule
{
    NSURL *url = [NSURL URLWithString:SCHEDULE_API_ENDPOINT];
    
    ILSchedule *schedule = [[ILSchedule alloc] init];
    
    ILScheduleXMLParserDelegate *xmlParserDelegate = [[ILScheduleXMLParserDelegate alloc] init];
    xmlParserDelegate.schedule = schedule;
    
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithContentsOfURL:url];
    [xmlParser setDelegate:xmlParserDelegate];
    
    if ([xmlParser parse])
    {
        NSLog(@"Schedule parsed successfully.");
    }
    else
    {
        NSLog(@"An ERROR occurred whilst parsing Schedule XML.");
    }    
    
    return schedule;
}

@end
