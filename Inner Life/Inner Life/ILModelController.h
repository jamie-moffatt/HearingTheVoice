//
//  ILModelController.h
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>

@class ILDataViewController;

@interface ILModelController : NSObject <UIPageViewControllerDataSource>

- (ILDataViewController *)viewControllerAtIndex:(NSUInteger)index storyboard:(UIStoryboard *)storyboard;
- (NSUInteger)indexOfViewController:(ILDataViewController *)viewController;

@end
