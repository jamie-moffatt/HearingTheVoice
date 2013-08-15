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
    NSURL* url = [NSURL URLWithString:QUESTION_API_ENDPOINT];
    NSString *questionXML = [NSString stringWithContentsOfURL:url encoding:NSUTF8StringEncoding error:nil];
    
    NSFileManager* fm = [[NSFileManager alloc] init];
    NSURL* appSupportDirURL = [fm URLForDirectory:NSApplicationSupportDirectory inDomain:NSUserDomainMask appropriateForURL:nil create:YES error:nil];
    [questionXML writeToURL:[appSupportDirURL URLByAppendingPathComponent:@"questions.xml"] atomically:YES encoding:NSUTF8StringEncoding error:nil];
    
    NSMutableArray *sectionList = [[NSMutableArray alloc] init];
    
    ILQuestionXMLParserDelegate *xmlParserDelegate = [[ILQuestionXMLParserDelegate alloc] init];
    xmlParserDelegate.sectionList = sectionList;
    
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:[questionXML dataUsingEncoding:NSUTF8StringEncoding]];
    [xmlParser setDelegate:xmlParserDelegate];
    
    if ([xmlParser parse])
    {
        NSLog(@"Questions parsed successfully from download.");
    }
    else
    {
        NSLog(@"An ERROR occurred whilst parsing Question XML from download.");
    }
    
    return [sectionList copy];
}

+ (ILSchedule *)downloadAndParseSchedule
{
    NSURL *url = [NSURL URLWithString:SCHEDULE_API_ENDPOINT];
    NSString *scheduleXML = [NSString stringWithContentsOfURL:url encoding:NSUTF8StringEncoding error:nil];
    
    NSFileManager* fm = [[NSFileManager alloc] init];
    NSURL* appSupportDirURL = [fm URLForDirectory:NSApplicationSupportDirectory inDomain:NSUserDomainMask appropriateForURL:nil create:YES error:nil];
    [scheduleXML writeToURL:[appSupportDirURL URLByAppendingPathComponent:@"schedule.xml"] atomically:YES encoding:NSUTF8StringEncoding error:nil];
    
    ILSchedule *schedule = [[ILSchedule alloc] init];
    
    ILScheduleXMLParserDelegate *xmlParserDelegate = [[ILScheduleXMLParserDelegate alloc] init];
    xmlParserDelegate.schedule = schedule;
    
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:[scheduleXML dataUsingEncoding:NSUTF8StringEncoding]];
    [xmlParser setDelegate:xmlParserDelegate];
    
    if ([xmlParser parse])
    {
        NSLog(@"Schedule parsed successfully from download.");
    }
    else
    {
        NSLog(@"An ERROR occurred whilst parsing Schedule XML from download.");
    }    
    
    return schedule;
}

@end
